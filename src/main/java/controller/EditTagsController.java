package controller;

import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import model.Album;
import model.Alerts;
import model.Photo;
import model.Photos;
import model.User;
import util.SceneNavigator;

/**
 * Edit tags and tag types for one photo.
 */
public class EditTagsController {

    @FXML
    private Button addTagButton;
    @FXML
    private Button deleteSelectedTagButton;
    @FXML
    private Button createTagTypeButton;
    @FXML private Button deleteTagTypeButton;
    @FXML TagsListController tagsListController;
    @FXML TagTypeListController tagTypeListController;

    private User user;
    private Photo photo;
    private String selectedTag;
    private String selectedTagType;
    private Photos app;
    private Album album;

    @FXML
    public void start(User user, Photos app, Photo photo, Album album) {
        tagsListController.start(user, app, photo);
        tagTypeListController.start(user, app, photo);
        this.user = user;
        this.photo = photo;
        this.app = app;
        this.album = album;

        tagsListController.tagsListView.setOnMouseClicked(e ->
                selectedTag = tagsListController.tagsListView.getSelectionModel().getSelectedItem());
        tagTypeListController.tagTypeListView.setOnMouseClicked(e ->
                selectedTagType = tagTypeListController.tagTypeListView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void deleteTagType() {
        if (selectedTagType == null) {
            Alerts.error("Error", "No tag type selected.", "Please select a tag type to delete.");
            return;
        }
        user.getTagTypes().remove(selectedTagType);
        tagTypeListController.deleteTagType(selectedTagType);
        selectedTagType = null;
        Alerts.info("Success", "Tag type deleted successfully.", "The tag type has been removed.");
    }

    @FXML
    private void addTag() {
        if (selectedTagType == null) {
            Alerts.error("Error", "No tag type selected.", "Please select a tag type first.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Adding Tag");
        dialog.setHeaderText("Previously tag type: " + selectedTagType);
        dialog.setContentText("Tag Value:");
        Optional<String> tagValueResult = dialog.showAndWait();
        if (tagValueResult.isEmpty()) {
            return;
        }
        String tagValue = tagValueResult.get().strip();
        if (tagValue.isEmpty()) {
            Alerts.error("Error", "Tag Value cannot be empty.", "Please enter a valid tag value.");
            return;
        }
        this.photo.addTag(selectedTagType.strip(), tagValue);
        tagsListController.addTag(selectedTagType.strip(), tagValue);
        Alerts.info("Success", "Tag added successfully.", "The tag has been added to the photo.");
    }

    @FXML
    private void createTagType() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Tag type");
        dialog.setContentText("Tag name:");
        Optional<String> tagType = dialog.showAndWait();
        if (tagType.isEmpty()) {
            return;
        }
        String tagTypeValue = tagType.get().strip();
        if (tagTypeValue.isEmpty()) {
            Alerts.error("Error", "Tag Type cannot be empty.", "Please enter a valid tag type.");
            return;
        }
        if (user.getTagTypes().contains(tagTypeValue)) {
            Alerts.error("Error", "Tag Type already exists.", "Choose a different tag type.");
            return;
        }
        user.getTagTypes().add(tagTypeValue);
        tagTypeListController.addTagType(tagTypeValue);
        Alerts.info("Success", "Tag type added successfully.", "You can now use this tag type to tag photos.");
    }

    @FXML
    private void deleteSelectedTag() {
        if (selectedTag == null || !selectedTag.contains(":")) {
            Alerts.error("Error", "No tag selected.", "Please select a tag to delete.");
            return;
        }
        String[] parts = selectedTag.split(":", 2);
        String tagType = parts[0].strip();
        String tagValue = parts[1].strip();
        this.photo.deleteTag(tagType, tagValue);
        tagsListController.deleteTag(tagType, tagValue);
        selectedTag = null;
        Alerts.info("Success", "Tag deleted successfully.", "The tag has been removed from the photo.");
    }

    @FXML
    private void backToGallery() {
        Stage stage = (Stage) addTagButton.getScene().getWindow();
        SceneNavigator.show(stage, "/view/gallery.fxml", controller -> {
            if (controller instanceof GalleryController galleryController) {
                galleryController.start(this.app, this.album, this.user);
            }
        });
    }
}
