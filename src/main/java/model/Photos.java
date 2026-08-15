package model;

import controller.LoginController;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import util.SceneNavigator;

/**
 * JavaFX application entry point. Persistable state lives in {@link AppState}.
 */
public class Photos extends Application {

    public static final String storeDir = "data";
    public static final String storeFile = "data.dat";

    AppState state;
    Stage primaryStage;

    /**
     * Users live on {@link AppState}; this delegates so unmigrated controllers still compile.
     */
    public List<User> getUsers() {
        return state.getUsers();
    }

    public AppState getState() {
        return state;
    }

    private void seedStockUser() {
        User stockUser = new User("stock");
        stockUser.createAlbum("stock");
        File stockPhotos = new File(dataDirectory(), "users/stock/photos");
        File[] photos = stockPhotos.listFiles();
        if (photos != null) {
            for (File photo : photos) {
                if (photo.isFile() && Photo.hasSupportedExtension(photo.getName())) {
                    stockUser.getAlbum("stock").addPhoto(photo.getAbsolutePath());
                }
            }
        }
        state.addUser(stockUser);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        AppState loaded = readState();
        if (loaded == null) {
            this.state = new AppState();
            seedStockUser();
        } else {
            this.state = loaded;
        }

        primaryStage.setTitle("Photo Album");
        showLogin(primaryStage);
        attachSaveOnClose(primaryStage);
    }

    /**
     * Saves state when the window is closed.
     */
    public void attachSaveOnClose(Stage stage) {
        stage.setOnCloseRequest(event -> {
            try {
                writeState(this.state);
            } catch (IOException e) {
                errorAlert("Error writing to file", "", "Error writing to file /data/data.dat");
            }
        });
    }

    /**
     * @return persisted state, or {@code null} if {@code data/data.dat} is missing (first run)
     * @throws IOException if the file exists but cannot be read
     * @throws ClassNotFoundException if the serialized class is unknown
     */
    public static AppState readState() throws IOException, ClassNotFoundException {
        File store = storeFile();
        if (!store.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(store))) {
            Object raw = ois.readObject();
            if (raw instanceof AppState appState) {
                return appState;
            }
            throw new IOException("Unexpected serialized type: " + raw.getClass().getName());
        }
    }

    /**
     * Writes {@code state} to {@code data/data.dat}, creating the data directory if needed.
     */
    public static void writeState(AppState state) throws IOException {
        File dir = dataDirectory();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Could not create data directory: " + dir.getAbsolutePath());
        }
        File store = new File(dir, storeFile);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(store))) {
            oos.writeObject(state);
        }
    }

    static File dataDirectory() {
        String override = System.getProperty("photos.data.dir");
        if (override != null && !override.isBlank()) {
            return new File(override);
        }
        return new File(storeDir);
    }

    static File storeFile() {
        return new File(dataDirectory(), storeFile);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void errorAlert(String title, String header, String content) {
        Alerts.error(title, header, content);
    }

    public static void infoAlert(String title, String header, String content) {
        Alerts.info(title, header, content);
    }

    /**
     * Writes state and loads login into the same {@link #primaryStage}.
     */
    public void logout() {
        try {
            writeState(this.state);
        } catch (Exception e) {
            errorAlert("Error writing to file", "", "Error writing to file /data/data.dat");
        }
        if (primaryStage == null) {
            return;
        }
        showLogin(primaryStage);
    }

    /**
     * Compatibility overload for unmigrated controllers that still pass the app.
     */
    public void logout(Photos app) {
        if (app != null && app != this) {
            this.state = app.state;
            if (this.primaryStage == null) {
                this.primaryStage = app.primaryStage;
            }
        }
        logout();
    }

    /**
     * Writes state then exits via {@link Platform#exit()}.
     */
    public void quit() {
        try {
            writeState(this.state);
        } catch (Exception e) {
            errorAlert("Error writing to file", "", "Error writing to file /data/data.dat");
        }
        Platform.exit();
    }

    private void showLogin(Stage stage) {
        SceneNavigator.show(stage, "/view/login.fxml", controller -> {
            if (controller instanceof LoginController login) {
                login.setApp(this);
            }
        });
    }
}
