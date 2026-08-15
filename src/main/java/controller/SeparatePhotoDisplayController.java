package controller;

// Java imports
import java.io.File;

// JavaFX imports
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// Project imports
import model.Photo;

/**
 * Controller class for the separate photo display view.
 * This class is responsible for displaying the selected photo in a separate window.
 * It displays the photo, caption, date, and tags of the selected photo.
 *
 * @author ks1686
 */
public class SeparatePhotoDisplayController {

    @FXML private ImageView imageView;
    @FXML private Label captionLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label tagsLabel;

    /**
     * Method to start the separate photo display view.
     * This method sets the image view, caption, date, and tags of the selected photo.
     *
     * @param selectedPhoto the selected photo to display
     */
    public void start(Photo selectedPhoto) {
        // get the selected photo file path
        String filepath = selectedPhoto.getFilePath();
        Image image = new Image(new File(filepath).toURI().toString());

        // set the image view
        imageView.setImage(image);
        // set the caption label
        captionLabel.setText(selectedPhoto.getCaption());


        // set the date label
        dateTimeLabel.setText(selectedPhoto.getDate().getTime().toString());

        // set the tags label
        tagsLabel.setText(selectedPhoto.getTags().toString());

    }
}

