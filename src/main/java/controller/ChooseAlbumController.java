package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Album;
import model.Alerts;
import model.Photo;
import model.Photos;
import model.User;
import util.SceneNavigator;

/**
 * Choose a destination album to copy or move a photo.
 */
public class ChooseAlbumController {

    public AnchorPane albumList;
    public Button backToPhotos;
    @FXML
    private Text titleText;
    @FXML
    protected Button selectAlbumButton;
    @FXML
    protected AlbumListController albumListController;

    private User user;
    private Photos app;
    private Album currentAlbum;
    private Photo selectedPhoto;

    public void start(Photos app, Album currentAlbum, Photo selectedPhoto, User user) {
        this.app = app;
        this.currentAlbum = currentAlbum;
        this.selectedPhoto = selectedPhoto;
        this.user = user;
        titleText.setText("Choose an album");
        albumListController.start(user, app);
    }

    @FXML
    public void backToGallery() {
        Stage stage = (Stage) selectAlbumButton.getScene().getWindow();
        SceneNavigator.show(stage, "/view/gallery.fxml", controller -> {
            if (controller instanceof GalleryController galleryController) {
                galleryController.start(this.app, this.currentAlbum, this.user);
            }
        });
    }

    @FXML
    public void moveOrCopyToAlbum() {
        String destName = albumListController.getSelectedAlbumName();
        if (destName == null) {
            Alerts.error("Error", "No album selected", "No album selected");
            return;
        }
        Album dest = user.getAlbum(destName);
        if (dest == null) {
            Alerts.error("Error", "No album selected", "No album selected");
            return;
        }
        if (dest.getAlbumName().equals(currentAlbum.getAlbumName())) {
            Alerts.error("Error", "Cannot copy to same album", "Cannot copy to same album");
            return;
        }
        if (dest.getPhotos().contains(selectedPhoto)) {
            Alerts.error("Error", "Photo already in album", "Photo already in album");
            return;
        }
        String buttonText = selectAlbumButton.getText();
        if ("Copy to Album".equals(buttonText)) {
            dest.addPhoto(selectedPhoto.copy());
            Alerts.info("Photo Copied", "", "Photo copied to album " + dest.getAlbumName());
        } else if ("Move to Album".equals(buttonText)) {
            dest.addPhoto(selectedPhoto);
            currentAlbum.removePhoto(selectedPhoto);
            Alerts.info("Photo moved", "", "Photo moved to album " + dest.getAlbumName());
        }
        backToGallery();
    }

    public Button getSelectAlbumButton() {
        return selectAlbumButton;
    }
}
