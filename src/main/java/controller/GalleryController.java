package controller;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Album;
import model.Alerts;
import model.Photo;
import model.Photos;
import model.User;
import util.SceneNavigator;

/**
 * Gallery of photos in one album.
 */
public class GalleryController {
    public Button removePhotoButton;
    public Button setCaptionButton;
    public Button displaySeparatelyButton;
    public Button editTagsButton;
    public Button backToAlbums;

    @FXML private Label galleryTitle;
    @FXML private Label gallerySubtitle;
    @FXML private Label emptyGalleryLabel;
    @FXML protected GalleryImageViewController galleryViewController;

    private Photos app;
    private Album album;
    private User user;

    @FXML
    private Button addPhotoButton;
    @FXML
    private Button copyToAlbumButton;
    @FXML
    private Button moveToAlbumButton;

    public User getUser() {
        return user;
    }

    public void start(Photos app, Album album, User user) {
        this.app = app;
        this.album = album;
        this.user = user;
        galleryTitle.setText(album.getAlbumName());
        refreshGallery();
    }

    private void refreshGallery() {
        galleryViewController.start(album);
        int count = album.getPhotos().size();
        gallerySubtitle.setText(count + (count == 1 ? " photo" : " photos"));
        boolean empty = count == 0;
        emptyGalleryLabel.setVisible(empty);
        emptyGalleryLabel.setManaged(empty);
        setPhotoActionsDisabled(empty);
    }

    private void setPhotoActionsDisabled(boolean empty) {
        removePhotoButton.setDisable(empty);
        setCaptionButton.setDisable(empty);
        displaySeparatelyButton.setDisable(empty);
        editTagsButton.setDisable(empty);
        copyToAlbumButton.setDisable(empty);
        moveToAlbumButton.setDisable(empty);
    }

    private Photo requireSelectedPhoto(String action) {
        Photo selected = galleryViewController.getSelectedPhoto();
        if (selected == null) {
            Alerts.error(action, "No photo selected", "Select a photo first.");
        }
        return selected;
    }

    public void addPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Photo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.bmp", "*.gif", "*.jpeg", "*.jpg", "*.png",
                        "*.BMP", "*.GIF", "*.JPEG", "*.JPG", "*.PNG"));
        File file = fileChooser.showOpenDialog(addPhotoButton.getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            Photo photo = Photo.importFile(file, user);
            album.addPhoto(photo);
            refreshGallery();
            // select the newly added photo so follow-up actions just work
            galleryViewController.getSelectedPhoto();
        } catch (IllegalArgumentException | IOException e) {
            Alerts.error("Invalid image", "Could not import photo", e.getMessage());
        }
    }

    @FXML
    public void removePhoto() {
        Photo selectedPhoto = requireSelectedPhoto("Remove Photo");
        if (selectedPhoto == null) {
            return;
        }
        album.removePhoto(selectedPhoto);
        refreshGallery();
    }

    @FXML
    public void setCaption() {
        Photo selectedPhoto = requireSelectedPhoto("Set Caption");
        if (selectedPhoto == null) {
            return;
        }
        TextInputDialog dialog = new TextInputDialog(selectedPhoto.getCaption());
        dialog.setTitle("Set caption");
        dialog.setHeaderText("Enter new caption for the selected photo (leave empty to delete caption):");
        dialog.setContentText("Enter caption:");
        String caption;
        try {
            caption = dialog.showAndWait().get();
        } catch (NoSuchElementException e) {
            return;
        }
        selectedPhoto.setCaption(caption);
        refreshGallery();
    }

    @FXML
    public void displaySeparately() {
        Photo selectedPhoto = requireSelectedPhoto("Display Separately");
        if (selectedPhoto == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/separatephotodisplay.fxml"));
            VBox root = loader.load();
            SeparatePhotoDisplayController controller = loader.getController();
            Stage stage = new Stage();
            stage.setTitle(selectedPhoto.getCaption() == null || selectedPhoto.getCaption().isBlank()
                    ? "Photo" : selectedPhoto.getCaption());
            Scene scene = new Scene(root, 800, 600);
            var css = getClass().getResource("/css/app.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            controller.start(selectedPhoto);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Alerts.error("Display Separately", "Failed to load separate photo display screen",
                    "Failed to load separate photo display screen");
        }
    }

    @FXML
    public void editTags() {
        Photo selectedPhoto = requireSelectedPhoto("Edit Tags");
        if (selectedPhoto == null) {
            return;
        }
        Stage stage = (Stage) editTagsButton.getScene().getWindow();
        SceneNavigator.show(stage, "/view/edittags.fxml", controller -> {
            if (controller instanceof EditTagsController editTagsController) {
                editTagsController.start(user, app, selectedPhoto, album);
            }
        });
    }

    @FXML
    public void copyToAlbum() {
        Photo selectedPhoto = requireSelectedPhoto("Copy to Album");
        if (selectedPhoto == null) {
            return;
        }
        openChooseAlbum(selectedPhoto, "Copy to Album", copyToAlbumButton);
    }

    @FXML
    public void moveToAlbum() {
        Photo selectedPhoto = requireSelectedPhoto("Move to Album");
        if (selectedPhoto == null) {
            return;
        }
        openChooseAlbum(selectedPhoto, "Move to Album", moveToAlbumButton);
    }

    private void openChooseAlbum(Photo selectedPhoto, String buttonText, Button source) {
        Stage stage = (Stage) source.getScene().getWindow();
        SceneNavigator.show(stage, "/view/choosealbum.fxml", controller -> {
            if (controller instanceof ChooseAlbumController chooseAlbumController) {
                chooseAlbumController.start(this.app, this.album, selectedPhoto, this.user);
                chooseAlbumController.getSelectAlbumButton().setText(buttonText);
            }
        });
    }

    @FXML
    public void backToHomepage() {
        Stage stage = (Stage) addPhotoButton.getScene().getWindow();
        SceneNavigator.show(stage, "/view/homepage.fxml", controller -> {
            if (controller instanceof HomepageController homepageController) {
                homepageController.start(this.user, this.app);
            }
        });
    }

    @FXML
    public void openSlideshow() {
        Photo selectedPhoto = requireSelectedPhoto("Open Slideshow");
        if (selectedPhoto == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/slideshowview.fxml"));
            VBox root = loader.load();
            SlideshowViewController slideshowViewController = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Slideshow — " + album.getAlbumName());
            Scene scene = new Scene(root, 900, 680);
            var css = getClass().getResource("/css/app.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            slideshowViewController.start(selectedPhoto, this.album);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Alerts.error("Open Slideshow", "Failed to load slideshow screen",
                    "Failed to load slideshow screen");
        }
    }
}
