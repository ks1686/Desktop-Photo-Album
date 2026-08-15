package util;

import java.io.IOException;
import java.util.function.Consumer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Loads an FXML view onto a stage. Scene switches should go through this helper.
 */
public final class SceneNavigator {

    private SceneNavigator() {}

    /**
     * Loads {@code fxmlPath} from the classpath, applies {@code controllerInit} to the
     * controller, then shows a 800x600 scene on {@code stage}.
     *
     * @throws IllegalStateException if the FXML cannot be loaded
     */
    public static void show(Stage stage, String fxmlPath, Consumer<Object> controllerInit) {
        FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlPath));
        final Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load FXML from classpath: " + fxmlPath, e);
        }
        if (controllerInit != null) {
            controllerInit.accept(loader.getController());
        }
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }
}
