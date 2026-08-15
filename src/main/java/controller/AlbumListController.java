package controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import model.Album;
import model.Alerts;
import model.Photos;
import model.User;

/**
 * Album list used on the homepage and the choose-album screen.
 */
public class AlbumListController {
    @FXML ListView<String> albumListView;
    private ObservableList<String> obsList;
    private User user;
    private Photos app;

    public void start(User user, Photos app) {
        this.user = user;
        this.app = app;
        refresh();
        if (!obsList.isEmpty()) {
            albumListView.getSelectionModel().select(0);
        }
    }

    public void refresh() {
        List<String> albumNames = new ArrayList<>();
        for (Album album : user.getAlbums()) {
            albumNames.add(formatAlbumRow(album));
        }
        obsList = FXCollections.observableArrayList(albumNames);
        albumListView.setItems(obsList);
    }

    private static String formatDate(Calendar date) {
        if (date == null) {
            return "N/A";
        }
        return (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.DAY_OF_MONTH) + "/" + date.get(Calendar.YEAR);
    }

    private static String formatAlbumRow(Album album) {
        return album.getAlbumName() + " (" + album.getPhotos().size() + " photos, "
                + formatDate(album.getStartDate()) + " - " + formatDate(album.getEndDate()) + ")";
    }

    public String getSelectedAlbum() {
        return albumListView.getSelectionModel().getSelectedItem();
    }

    public String getSelectedAlbumName() {
        String selected = getSelectedAlbum();
        if (selected == null) {
            return null;
        }
        return fixAlbumName(selected);
    }

    public void deleteAlbum(String albumName) {
        Album album = user.getAlbum(albumName);
        if (album == null) {
            Alerts.error("Error", "Album not found", "The album could not be found.");
            return;
        }
        user.getAlbums().remove(album);
        refresh();
        Alerts.info("Album Deleted", "", "Album " + albumName + " has been deleted.");
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

    public String fixAlbumName(String albumName) {
        int index = albumName.indexOf("(");
        if (index != -1) {
            return albumName.substring(0, index - 1);
        }
        return albumName;
    }
}
