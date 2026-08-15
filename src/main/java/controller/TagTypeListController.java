package controller;

//Java imports
import java.util.ArrayList;
import java.util.List;

//JavaFX imports
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

//Project imports
import model.Photo;
import model.Photos;
import model.User;

/**
 * Controller for the list of tag types
 * This class is responsible for displaying the list of tag types in the GUI
 * It is also responsible for adding and deleting tag types from the list
 *
 * @author jacobjude
 */
public class TagTypeListController {

    @FXML
    protected ListView<String> tagTypeListView;
    private ObservableList<String> obsList;

    /**
     * Initializes the controller class.
     * @param user: the current user
     * @param app: the main application
     * @param photo: the current photo
     */
    @FXML
    public void start(User user, Photos app, Photo photo) {
    

        List<String> tagTypes = user.getTagTypes();
        List<String> tags = new ArrayList<>();
        for (String tagType: tagTypes) {
            // tagname is key and tagvalue is value
            tags.add(tagType);
            
        }
        obsList = FXCollections.observableArrayList(tags); // create an observable list from the list of albums
        tagTypeListView.setItems(obsList); // set the list view to the observable list

    }

    /**
     * Adds a tag type to the list
     * @param tagType: the tag type to add
     */
    public void addTagType(String tagType) {
        obsList.add(tagType);
    }

    /**
     * Deletes a tag type from the list
     * @param tagType: the tag type to delete
     */
    public void deleteTagType(String tagType) {
        obsList.remove(tagType);
    }
}