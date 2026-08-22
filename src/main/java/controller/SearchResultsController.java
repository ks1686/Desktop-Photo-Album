package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import model.Album;
import model.Alerts;
import model.Photo;
import model.Photos;
import model.User;
import util.SceneNavigator;

/**
 * Search results gallery that can become a new album.
 */
public class SearchResultsController {

    public Button backToHomepageButton;
    @FXML
    private Button createAlbumButton;
    @FXML private Label resultsSubtitle;
    @FXML private Label emptyResultsLabel;
    @FXML private GalleryImageViewController galleryViewController;

    private Photos app;
    private User user;
    private Album searchResultsAlbum;

    @FXML
    public void start(Photos app, User currentUser, Album searchResultsAlbum) {
        this.app = app;
        this.user = currentUser;
        this.searchResultsAlbum = searchResultsAlbum;
        galleryViewController.start(searchResultsAlbum);
        boolean empty = searchResultsAlbum.getPhotos().isEmpty();
        emptyResultsLabel.setVisible(empty);
        emptyResultsLabel.setManaged(empty);
        createAlbumButton.setDisable(empty);
        setSummary("", searchResultsAlbum.getPhotos().size());
    }

    /**
     * Shows what was searched and how many photos matched.
     */
    public void setSummary(String query, int matchCount) {
        if (resultsSubtitle == null) {
            return;
        }
        String shownQuery = query == null || query.isBlank() ? "all photos" : "\"" + query + "\"";
        resultsSubtitle.setText(matchCount + (matchCount == 1 ? " photo matches " : " photos match ")
                + shownQuery);
    }

    @FXML
    private void createAlbum() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Album");
        dialog.setHeaderText("Create a new album from these search results");
        dialog.setContentText("Enter the name of the new album:");
        dialog.showAndWait().ifPresent(albumName -> {
            if (!user.createAlbum(albumName)) {
                Alerts.error("Create Album", "Could not create album",
                        "The album name is invalid or already exists.");
                return;
            }
            Album userAlbum = user.getAlbum(albumName);
            for (Photo photo : searchResultsAlbum.getPhotos()) {
                userAlbum.addPhoto(photo.copy());
            }
            Stage stage = (Stage) createAlbumButton.getScene().getWindow();
            SceneNavigator.show(stage, "/view/homepage.fxml", controller -> {
                if (controller instanceof HomepageController homepageController) {
                    homepageController.start(user, app);
                }
            });
        });
    }

    @FXML
    public void backToHomepage() {
        Stage stage = (Stage) backToHomepageButton.getScene().getWindow();
        SceneNavigator.show(stage, "/view/homepage.fxml", controller -> {
            if (controller instanceof HomepageController homepageController) {
                homepageController.start(this.user, this.app);
            }
        });
    }
}
