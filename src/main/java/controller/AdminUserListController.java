package controller;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import model.Photos;
import model.User;

/**
 * Admin user list. The parent homepage injects the delete button.
 */
public class AdminUserListController {
    @FXML ListView<String> adminUserListView;
    public ObservableList<String> obsList;
    private Button deleteUserButton;

    public void setDeleteUserButton(Button deleteUserButton) {
        this.deleteUserButton = deleteUserButton;
    }

    public void start(Photos app) {
        List<String> users = new ArrayList<>();
        for (User user : app.getUsers()) {
            users.add(user.getUsername());
        }
        obsList = FXCollections.observableArrayList(users);
        adminUserListView.setItems(obsList);
        adminUserListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> updateDeleteButton(newVal));
        updateDeleteButton(adminUserListView.getSelectionModel().getSelectedItem());
    }

    private void updateDeleteButton(String selectedUser) {
        if (deleteUserButton != null) {
            deleteUserButton.setDisable(selectedUser == null);
        }
    }
}
