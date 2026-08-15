package controller;

import java.util.List;
import java.util.NoSuchElementException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Album;
import model.Alerts;
import model.Photo;
import model.Photos;
import model.User;
import util.SceneNavigator;

/**
 * User homepage: albums, search, logout, quit.
 */
public class HomepageController {
    public AnchorPane albumList;
    public Button deleteAlbumButton;
    public Button logoutButton;
    public Button renameAlbumButton;
    public Button createAlbumButton;
    public Button openAlbumButton;
    public Button quitButton;
    @FXML
    protected AlbumListController albumListController;

    @FXML protected TextField searchBarTextField;
    private Photos app;
    private User user;

    public void start(User user, Photos app) {
        albumListController.start(user, app);
        this.app = app;
        this.user = user;
    }

    @FXML
    private void deleteAlbum() {
        String albumName = albumListController.getSelectedAlbumName();
        if (albumName == null) {
            Alerts.error("Error", "No album selected", "Please select an album to delete.");
            return;
        }
        albumListController.deleteAlbum(albumName);
    }

    @FXML
    private void logout() {
        app.logout();
    }

    @FXML
    private void renameAlbum() {
        String albumName = albumListController.getSelectedAlbumName();
        if (albumName == null) {
            Alerts.error("Error", "No album selected", "Please select an album to rename.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog(albumName);
        dialog.setTitle("Rename Album");
        dialog.setHeaderText("Enter the new album name:");
        dialog.setContentText("New album name:");
        String newAlbumName;
        try {
            newAlbumName = dialog.showAndWait().get();
        } catch (NoSuchElementException e) {
            return;
        }
        albumListController.renameAlbum(albumName, newAlbumName);
    }

    @FXML
    public void createAlbum() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Album");
        dialog.setHeaderText("Enter the name of the new album:");
        dialog.setContentText("Album name:");
        String albumName;
        try {
            albumName = dialog.showAndWait().get();
        } catch (NoSuchElementException e) {
            return;
        }
        albumListController.createAlbum(albumName);
    }

    private boolean isValidSearchQuery(String query) {
        if (query == null || query.isEmpty()) {
            return false;
        }
        return query.matches("\\d{2}/\\d{2}/\\d{4}-\\d{2}/\\d{2}/\\d{4}")
                || query.matches("[^=\\s]+=.+")
                || query.matches("[^=\\s]+=.+ (AND|OR) [^=\\s]+=.+");
    }

    @FXML
    public void searchPhotos() {
        String query = searchBarTextField.getText();
        if (!isValidSearchQuery(query)) {
            Alerts.error("Invalid Search Query", "Invalid Search Query", "Invalid Search Query");
            return;
        }
        List<Photo> photos;
        try {
            photos = user.searchAlbums(query);
        } catch (IllegalArgumentException e) {
            Alerts.error("Search Error", "Search query is invalid.",
                    "Hover over search bar and see the tooltip for more information.");
            return;
        }

        String tempAlbumName = "Search Results";
        int count = 1;
        String uniqueAlbumName = tempAlbumName;
        while (user.getAlbum(uniqueAlbumName) != null) {
            uniqueAlbumName = tempAlbumName + count;
            count++;
        }
        Album searchResults = new Album(uniqueAlbumName, photos);
        Stage stage = (Stage) albumListController.albumListView.getScene().getWindow();
        SceneNavigator.show(stage, "/view/searchresults.fxml", controller -> {
            if (controller instanceof SearchResultsController searchResultsController) {
                searchResultsController.start(app, user, searchResults);
            }
        });
    }

    @FXML
    public void quit() {
        app.quit();
    }

    @FXML
    public void openAlbum() {
        String albumName = albumListController.getSelectedAlbumName();
        if (albumName == null) {
            Alerts.error("Open Album", "Failed to open album", "No album selected");
            return;
        }
        Album album = user.getAlbum(albumName);
        if (album == null) {
            Alerts.error("Open Album", "Failed to open album", "Failed to open album");
            return;
        }
        Stage stage = (Stage) albumListController.albumListView.getScene().getWindow();
        SceneNavigator.show(stage, "/view/gallery.fxml", controller -> {
            if (controller instanceof GalleryController galleryController) {
                galleryController.start(app, album, user);
            }
        });
    }
}
