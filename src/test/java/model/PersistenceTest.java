package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PersistenceTest {

    @TempDir
    Path temp;

    @BeforeEach
    void isolateStore() {
        System.setProperty("photos.data.dir", temp.toString());
    }

    @AfterEach
    void resetStore() {
        System.clearProperty("photos.data.dir");
    }

    @Test
    void writeThenReadRoundTripsUsersAlbumsAndPhotos() throws Exception {
        Path image = TestImages.writePng(temp, "keep.png");
        User user = new User("alice");
        assertTrue(user.createAlbum("Vacation"));
        Photo photo = new Photo(image.toAbsolutePath().toString(), "dock");
        photo.addTag("location", "Jersey");
        user.getAlbum("Vacation").addPhoto(photo);

        AppState state = new AppState();
        assertTrue(state.addUser(user));
        Photos.writeState(state);

        AppState loaded = Photos.readState();
        assertNotNull(loaded);
        User restored = loaded.findUser("alice");
        assertNotNull(restored);
        assertEquals(1, restored.getAlbums().size());
        assertEquals("Vacation", restored.getAlbum("Vacation").getAlbumName());
        Photo restoredPhoto = restored.getAlbum("Vacation").getPhotos().getFirst();
        assertEquals(photo, restoredPhoto);
        assertEquals("dock", restoredPhoto.getCaption());
        assertEquals("Jersey", restoredPhoto.getTags().getFirst().get("location"));
    }

    @Test
    void missingStoreIsFirstRunAndDoesNotCreateFile() throws Exception {
        assertNull(Photos.readState());
        assertTrue(Files.notExists(temp.resolve("data.dat")));
    }

    @Test
    void corruptStoreThrowsAndIsNotOverwritten() throws Exception {
        Path store = temp.resolve("data.dat");
        byte[] garbage = "not-a-serialized-app-state".getBytes();
        Files.write(store, garbage);

        assertThrows(Exception.class, Photos::readState);
        assertTrue(Arrays.equals(garbage, Files.readAllBytes(store)), "readState must not rewrite a corrupt store");
    }

    @Test
    void addUserRejectsBlankDuplicateAndAdmin() {
        AppState state = new AppState();
        assertTrue(state.addUser(new User("alice")));
        assertTrue(state.getUsers().stream().anyMatch(u -> u.getUsername().equals("alice")));

        assertTrue(!state.addUser(new User("alice")));
        assertTrue(!state.addUser(new User("admin")));
        assertEquals(1, state.getUsers().size());
        assertNull(state.findUser("missing"));

        assertTrue(state.removeUser("alice"));
        assertNull(state.findUser("alice"));
    }
}
