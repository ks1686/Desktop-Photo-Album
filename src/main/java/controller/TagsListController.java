package controller;

// Java imports
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// JavaFX imports
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

// Project imports
import model.Photo;
import model.Photos;
import model.User;

/**
 * Controller for the TagsList.fxml
 * This controller is used to display the tags of a photo in a list view
 *
 * @author jacobjude
 */
public class TagsListController {

    @FXML
    protected ListView<String> tagsListView;
    private ObservableList<String> obsList;

    /**
     * Initializes the controller
     * @param user: the current user
     * @param app: the main application
     * @param photo: the photo whose tags are to be displayed
     */
    @FXML
    public void start(User user, Photos app, Photo photo) {

        List<Map<String, String>> photoTags = photo.getTags();
        List<String> tags = new ArrayList<>();

        for (Map<String, String> tagType: photoTags) {
            // tagname is key and tagvalue is value
            for (Map.Entry<String, String> tag : tagType.entrySet()) {
                tags.add(tag.getKey() + ": " + tag.getValue());
            }
        }
        obsList = FXCollections.observableArrayList(tags); // create an observable list from the list of albums
        tagsListView.setItems(obsList); // set the list view to the observable list

    }

    /**
     * Adds a tag to the list view
     * @param tagType: the type of the tag
     * @param tagValue: the value of the tag
     */
    public void addTag(String tagType, String tagValue) {
        obsList.add(tagType + ": " + tagValue);
    }

    /**
     * Deletes a tag from the list view
     * @param tagType: the type of the tag
     * @param tagValue: the value of the tag
     */
    public void deleteTag(String tagType, String tagValue) {
        obsList.remove(tagType + ": " + tagValue);
    }
}