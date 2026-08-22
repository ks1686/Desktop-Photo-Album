package controller;

import java.time.format.FormatStyle;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.Album;
import model.Alerts;
import model.Photos;
import model.User;

/**
 * Album list used on the homepage and the choose-album screen. The ListView is
 * typed to {@link Album} directly so selection never depends on display text.
 */
public class AlbumListController {
    @FXML private ListView<Album> albumListView;
    @FXML private Label emptyLabel;

    private User user;
    private Photos app;

    public void start(User user, Photos app) {
        this.user = user;
        this.app = app;
        refresh();
        if (!albumListView.getItems().isEmpty()) {
            albumListView.getSelectionModel().select(0);
        }
    }

    public void refresh() {
        ObservableList<Album> items = FXCollections.observableArrayList(user.getAlbums());
        albumListView.setItems(items);
        albumListView.setCellFactory(view -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Album album, boolean empty) {
                super.updateItem(album, empty);
                setText(empty || album == null ? null : formatAlbumRow(album));
            }
        });
        boolean empty = items.isEmpty();
        emptyLabel.setVisible(empty);
        emptyLabel.setManaged(empty);
        albumListView.setVisible(!empty);
        albumListView.setManaged(!empty);
    }

    private static String formatDate(java.util.Calendar date) {
        if (date == null) {
            return "N/A";
        }
        return date.toInstant().atZone(date.getTimeZone().toZoneId()).toLocalDate()
                .format(java.time.format.DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    private static String formatAlbumRow(Album album) {
        return album.getAlbumName() + "  ·  " + album.getPhotos().size()
                + (album.getPhotos().size() == 1 ? " photo" : " photos")
                + "  ·  " + formatDate(album.getStartDate()) + " – " + formatDate(album.getEndDate());
    }

    /** @return the selected {@link Album}, or {@code null} when nothing is selected */
    public Album getSelectedAlbum() {
        return albumListView.getSelectionModel().getSelectedItem();
    }

    /**
     * @return the selected album's name, or {@code null} when nothing is selected
     */
    public String getSelectedAlbumName() {
        Album selected = getSelectedAlbum();
        return selected == null ? null : selected.getAlbumName();
    }

    public void deleteAlbum(String albumName) {
        Album album = user.getAlbum(albumName);
        if (album == null) {
            Alerts.error("Error", "Album not found", "The album could not be found.");
            return;
        }
        user.getAlbums().remove(album);
        refresh();
        Alerts.info("Album Deleted", "", "Album \"" + albumName + "\" has been deleted.");
    }

    public void renameAlbum(String albumName, String newAlbumName) {
        if (newAlbumName == null || newAlbumName.isBlank() || newAlbumName.equals(albumName)
                || user.getAlbum(newAlbumName) != null) {
            Alerts.error("Error", "Invalid Album Name", "The album name is invalid.");
            return;
        }
        user.getAlbum(albumName).setAlbumName(newAlbumName);
        refresh();
    }

    public void createAlbum(String albumName) {
        if (albumName == null || albumName.isBlank()) {
            Alerts.error("Error", "Invalid Album Name",
                    "The album name is invalid. Make sure the name has non-whitespace characters.");
            return;
        }
        if (!user.createAlbum(albumName)) {
            Alerts.error("Error", "Invalid Album Name", "The album name is invalid. The album already exists.");
            return;
        }
        refresh();
    }

    public ListView<Album> getAlbumListView() {
        return albumListView;
    }
}
