package controller;

import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Alerts;
import model.Photos;
import model.User;

/**
 * Admin homepage: create and delete users, then logout on the same stage.
 */
public class AdminHomepageController {
    public AnchorPane adminUserList;
    @FXML protected AdminUserListController adminUserListController;

    @FXML Button createUserButton;
    @FXML Button deleteUserButton;

    private Photos app;

    public void start(Stage stage, Photos app) {
        this.app = app;
        adminUserListController.setDeleteUserButton(deleteUserButton);
        adminUserListController.start(app);
    }

    public void createUser() {
        Optional<String> result = showItemInputDialog((Stage) createUserButton.getScene().getWindow());
        if (result.isEmpty()) {
            return;
        }
        String username = result.get().strip();
        if (username.isEmpty()) {
            Alerts.error("Error", "Invalid Username", "Username cannot be empty.");
            return;
        }
        if ("admin".equals(username)) {
            Alerts.error("Error", "Reserved Username", "The username admin is reserved.");
            return;
        }
        if (!app.getState().addUser(new User(username))) {
            Alerts.error("Error", "Username Already Exists",
                    "The username you entered already exists. Please enter a different username.");
            return;
        }
        adminUserListController.obsList.add(username);
        Alerts.info("User Created", "User Created Successfully",
                "The user " + username + " was created successfully.");
    }

    public void deleteUser() {
        String selectedUsername = adminUserListController.adminUserListView.getSelectionModel().getSelectedItem();
        if (selectedUsername == null) {
            Alerts.error("Error", "No user selected", "Select a user to delete.");
            return;
        }
        if (app.getState().removeUser(selectedUsername)) {
            adminUserListController.obsList.remove(selectedUsername);
            Alerts.info("User Deleted", "User Deleted Successfully",
                    "The user " + selectedUsername + " was deleted successfully.");
        }
    }

    private Optional<String> showItemInputDialog(Stage mainStage) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(mainStage);
        dialog.setTitle("Create New User");
        dialog.setHeaderText("Enter Username of New User");
        dialog.setContentText("Username: ");
        return dialog.showAndWait();
    }

    @FXML
    public void logout() {
        app.logout();
    }
}
