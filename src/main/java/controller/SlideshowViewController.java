package controller;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Album;
import model.Photo;

/**
 * Manual next/prev slideshow. Wrap is always allowed when the album has more than one photo.
 */
public class SlideshowViewController {

    private Album currentAlbum;
    private int currentIndex;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private ImageView slideshowImageView;

    public void start(Photo selectedPhoto, Album currentAlbum) {
        this.currentAlbum = currentAlbum;
        this.currentIndex = currentAlbum.getPhotos().indexOf(selectedPhoto);
        if (this.currentIndex < 0) {
            this.currentIndex = 0;
        }
        showCurrent();
        updateButtons();
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

    private void showCurrent() {
        Photo currentPhoto = currentAlbum.getPhotos().get(currentIndex);
        Image image = new Image(new File(currentPhoto.getFilePath()).toURI().toString());
        slideshowImageView.setImage(image);
    }

    private void updateButtons() {
        boolean multi = currentAlbum.getPhotos().size() > 1;
        nextButton.setDisable(!multi);
        previousButton.setDisable(!multi);
    }
}
