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

    /** App-wide stylesheet, applied to every scene this class creates. */
    public static final String STYLESHEET = "/css/app.css";

    private SceneNavigator() {}

    /**
     * Loads {@code fxmlPath} from the classpath, applies {@code controllerInit} to the
     * controller, then shows a resizable 1000x700 scene on {@code stage}.
     *
     * @throws IllegalStateException if the FXML cannot be loaded
     */
    public static void show(Stage stage, String fxmlPath, Consumer<Object> controllerInit) {
        show(stage, fxmlPath, controllerInit, 1000, 700);
    }

    /**
     * As {@link #show(Stage, String, Consumer)} with an explicit initial size.
     */
    public static void show(Stage stage, String fxmlPath, Consumer<Object> controllerInit, int width, int height) {
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
        Scene scene = new Scene(root, width, height);
        var css = SceneNavigator.class.getResource(STYLESHEET);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
