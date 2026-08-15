package model;

import javafx.scene.control.Alert;

/**
 * Shared JavaFX alert helpers. Controllers should prefer this over {@link Photos}
 * once they are migrated.
 */
public final class Alerts {

    private Alerts() {}

    /**
     * Shows a blocking error alert.
     */
    public static void error(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Shows a blocking information alert.
     */
    public static void info(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
