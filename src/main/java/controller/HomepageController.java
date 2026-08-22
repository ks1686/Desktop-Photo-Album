package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.NoSuchElementException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
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
    public StackPane albumList;
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
        this.app = app;
        this.user = user;
        albumListController.start(user, app);
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
        Album selected = albumListController.getSelectedAlbum();
        if (selected == null) {
            Alerts.error("Error", "No album selected", "Please select an album to rename.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog(selected.getAlbumName());
        dialog.setTitle("Rename Album");
        dialog.setHeaderText("Enter the new album name:");
        dialog.setContentText("New album name:");
        String newAlbumName;
        try {
            newAlbumName = dialog.showAndWait().get();
        } catch (NoSuchElementException e) {
            return;
        }
        albumListController.renameAlbum(selected.getAlbumName(), newAlbumName);
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

    /**
     * A valid query is a date range (M/D/YYYY-M/D/YYYY, real calendar dates),
     * a single tag (key=value), or two tags joined by AND/OR.
     */
    private boolean isValidSearchQuery(String query) {
        if (query == null || query.isBlank()) {
            return false;
        }
        query = query.strip();
        if (isRealDateRange(query)) {
            return true;
        }
        return query.matches("[^=\\s]+=.+")
                || query.matches("[^=\\s]+=.+ (AND|OR) [^=\\s]+=.+");
    }

    private static final DateTimeFormatter LENIENT_DATE = DateTimeFormatter
            .ofPattern("M/d/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private boolean isRealDateRange(String query) {
        String[] parts = query.split("-", 2);
        if (parts.length != 2) {
            return false;
        }
        try {
            LocalDate start = LocalDate.parse(parts[0].strip(), LENIENT_DATE);
            LocalDate end = LocalDate.parse(parts[1].strip(), LENIENT_DATE);
            return !start.isAfter(end);
        } catch (java.time.format.DateTimeParseException | ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    @FXML
    public void searchPhotos() {
        String query = searchBarTextField.getText();
        if (!isValidSearchQuery(query)) {
            Alerts.error("Invalid Search Query", "Invalid search query",
                    "Use MM/DD/YYYY-MM/DD/YYYY for a date range, tag=value for a tag,\n"
                    + "or combine two tags with AND / OR. Hover the search bar for examples.");
            return;
        }
        List<Photo> photos;
        try {
            photos = user.searchAlbums(query.strip());
        } catch (IllegalArgumentException e) {
            Alerts.error("Search Error", "Search query is invalid.",
                    "Hover over the search bar for format examples.");
            return;
        }

        // PhotoSearch parses date ranges by splitting on "-", which breaks when the
        // range itself was typed loosely; normalize to zero-padded form first.
        String normalized = normalizeDateRange(query.strip());

        String tempAlbumName = "Search Results";
        int count = 1;
        String uniqueAlbumName = tempAlbumName;
        while (user.getAlbum(uniqueAlbumName) != null) {
            uniqueAlbumName = tempAlbumName + count;
            count++;
        }
        Album searchResults = new Album(uniqueAlbumName, photos);
        Stage stage = (Stage) searchBarTextField.getScene().getWindow();
        SceneNavigator.show(stage, "/view/searchresults.fxml", controller -> {
            if (controller instanceof SearchResultsController searchResultsController) {
                searchResultsController.start(app, user, searchResults);
                searchResultsController.setSummary(normalized, photos.size());
            }
        });
    }

    /**
     * Rewrites a date-range query into strict MM/DD/YYYY-MM/DD/YYYY so the
     * model's parser can split on "-" safely. Non-range queries pass through.
     */
    private static String normalizeDateRange(String query) {
        String[] parts = query.split("-", 2);
        if (parts.length != 2 || parts[0].indexOf('/') < 0) {
            return query;
        }
        try {
            DateTimeFormatter out = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            return LocalDate.parse(parts[0].strip(), LENIENT_DATE).format(out)
                    + "-" + LocalDate.parse(parts[1].strip(), LENIENT_DATE).format(out);
        } catch (java.time.format.DateTimeParseException e) {
            return query;
        }
    }

    @FXML
    public void quit() {
        app.quit();
    }

    @FXML
    public void openAlbum() {
        Album album = albumListController.getSelectedAlbum();
        if (album == null) {
            Alerts.error("Open Album", "Failed to open album", "No album selected");
            return;
        }
        Stage stage = (Stage) searchBarTextField.getScene().getWindow();
        SceneNavigator.show(stage, "/view/gallery.fxml", controller -> {
            if (controller instanceof GalleryController galleryController) {
                galleryController.start(app, album, user);
            }
        });
    }
}
