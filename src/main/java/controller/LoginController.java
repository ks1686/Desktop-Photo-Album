package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Alerts;
import model.Photos;
import model.User;
import util.SceneNavigator;

/**
 * Controller for the login screen.
 */
public class LoginController {

    public Text loginText;
    @FXML
    private TextField usernameTextField;

    private Photos app;

    public void setApp(Photos app) {
        this.app = app;
    }

    public void handleLogin() {
        String username = usernameTextField.getText() == null ? "" : usernameTextField.getText().strip();
        if (username.isEmpty()) {
            Alerts.error("Login Error", "Username required", "Enter a username.");
            return;
        }

        Stage stage = (Stage) usernameTextField.getScene().getWindow();
        if ("admin".equals(username)) {
            SceneNavigator.show(stage, "/view/adminhomepage.fxml", controller -> {
                if (controller instanceof AdminHomepageController adminController) {
                    adminController.start(stage, this.app);
                }
            });
            return;
        }

        User currentUser = null;
        for (User user : app.getUsers()) {
            if (user.getUsername().equals(username)) {
                currentUser = user;
                break;
            }
        }
        if (currentUser == null) {
            Alerts.error("Login Error", "User does not exist", "User does not exist");
            return;
        }
        User loggedIn = currentUser;
        SceneNavigator.show(stage, "/view/homepage.fxml", controller -> {
            if (controller instanceof HomepageController homepageController) {
                homepageController.start(loggedIn, app);
            }
        });
    }
}
