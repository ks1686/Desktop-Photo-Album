package launcher;

import model.Photos;

/**
 * Plain entry point for the runnable jar. JavaFX refuses to launch an
 * {@link javafx.application.Application} subclass directly from a jar
 * ("JavaFX runtime components are missing"), so this indirection is required.
 */
public final class Launcher {

    private Launcher() {}

    public static void main(String[] args) {
        Photos.main(args);
    }
}
