package controller;

import java.io.File;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Album;
import model.Photo;

/**
 * Slideshow in a dark theater-style window: arrow keys navigate, space
 * toggles autoplay, escape closes. Wraps at both ends.
 */
public class SlideshowViewController {

    private static final double AUTOPLAY_SECONDS = 3.0;

    private Album currentAlbum;
    private int currentIndex;
    private Timeline autoplay;
    private boolean playing;

    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button autoplayButton;
    @FXML private ImageView slideshowImageView;
    @FXML private Label photoInfoLabel;
    @FXML private Label hintLabel;

    public void start(Photo selectedPhoto, Album currentAlbum) {
        this.currentAlbum = currentAlbum;
        this.currentIndex = currentAlbum.getPhotos().indexOf(selectedPhoto);
        if (this.currentIndex < 0) {
            this.currentIndex = 0;
        }
        showCurrent();
        updateButtons();
        installKeyHandlers();
        slideshowImageView.requestFocus();
    }

    @FXML
    public void nextPhoto() {
        int size = currentAlbum.getPhotos().size();
        if (size == 0) {
            return;
        }
        currentIndex = (currentIndex + 1) % size;
        showCurrent();
        updateButtons();
    }

    @FXML
    public void previousPhoto() {
        int size = currentAlbum.getPhotos().size();
        if (size == 0) {
            return;
        }
        currentIndex = (currentIndex - 1 + size) % size;
        showCurrent();
        updateButtons();
    }

    @FXML
    public void toggleAutoplay() {
        if (playing) {
            stopAutoplay();
            return;
        }
        autoplay = new Timeline(new KeyFrame(Duration.seconds(AUTOPLAY_SECONDS), e -> nextPhoto()));
        autoplay.setCycleCount(Timeline.INDEFINITE);
        autoplay.play();
        playing = true;
        updateAutoplayButton();
    }

    private void stopAutoplay() {
        if (autoplay != null) {
            autoplay.stop();
        }
        playing = false;
        updateAutoplayButton();
    }

    private void updateAutoplayButton() {
        autoplayButton.setText(playing ? "⏸ Pause" : "▶ Play");
    }

    private void installKeyHandlers() {
        slideshowImageView.setFocusTraversable(true);
        slideshowImageView.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.RIGHT || code == KeyCode.LEFT) {
                if (!playing) {
                    if (code == KeyCode.RIGHT) {
                        nextPhoto();
                    } else {
                        previousPhoto();
                    }
                }
                event.consume();
            } else if (code == KeyCode.SPACE) {
                toggleAutoplay();
                event.consume();
            } else if (code == KeyCode.ESCAPE) {
                stopAutoplay();
                if (slideshowImageView.getScene() != null
                        && slideshowImageView.getScene().getWindow() instanceof Stage stage) {
                    stage.close();
                }
            }
        });
    }

    private void showCurrent() {
        Photo currentPhoto = currentAlbum.getPhotos().get(currentIndex);
        Image image = new Image(new File(currentPhoto.getFilePath()).toURI().toString());
        slideshowImageView.setImage(image);

        String caption = currentPhoto.getCaption();
        String label = caption != null && !caption.isBlank()
                ? caption + " — " : "";
        photoInfoLabel.setText(label + (currentIndex + 1) + " of "
                + currentAlbum.getPhotos().size());
    }

    private void updateButtons() {
        boolean multi = currentAlbum.getPhotos().size() > 1;
        nextButton.setDisable(!multi);
        previousButton.setDisable(!multi);
        autoplayButton.setDisable(!multi);
    }
}
