package model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PhotoTest {

    @TempDir
    Path temp;

    @AfterEach
    void resetDataDir() {
        System.clearProperty("photos.data.dir");
    }

    @Test
    void copyMutationDoesNotLeakToOriginal() throws Exception {
        Path file = TestImages.writePng(temp, "orig.png");
        Photo original = new Photo(file.toAbsolutePath().toString(), "beach");
        original.addTag("person", "Ada");

        Photo copy = original.copy();
        assertNotSame(original, copy);
        assertEquals(original, copy);
        assertEquals("beach", copy.getCaption());

        copy.setCaption("mutated");
        copy.addTag("location", "Paris");
        copy.getTags().clear();
        copy.getDate().add(Calendar.DAY_OF_MONTH, 3);

        assertEquals("beach", original.getCaption());
        assertEquals(1, original.getTags().size());
        assertEquals("Ada", original.getTags().getFirst().get("person"));
    }

    @Test
    void setCaptionAllowsEmptyAndRejectsNull() throws Exception {
        Path file = TestImages.writePng(temp, "caption.png");
        Photo photo = new Photo(file.toAbsolutePath().toString(), "old");

        photo.setCaption("");
        assertEquals("", photo.getCaption());
        assertThrows(NullPointerException.class, () -> photo.setCaption(null));
    }

    @Test
    void equalsUsesNormalizedAbsolutePath() throws Exception {
        Path file = TestImages.writePng(temp, "same.png");
        String abs = file.toAbsolutePath().toString();
        Photo a = new Photo(abs, "one");
        Photo b = new Photo(file.toString(), "two");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        Path other = TestImages.writePng(temp, "other.png");
        assertNotEquals(a, new Photo(other.toAbsolutePath().toString()));
    }

    @Test
    void acceptsCaseInsensitiveExtensions() throws Exception {
        Path file = TestImages.writePng(temp, "UPPER.PNG");
        Photo photo = new Photo(file.toAbsolutePath().toString());
        assertEquals(0, photo.getDate().get(Calendar.HOUR_OF_DAY));
    }

    @Test
    void importFileCopiesIntoUserPhotosDirectory() throws Exception {
        System.setProperty("photos.data.dir", temp.toString());
        Path src = TestImages.writePng(temp.resolve("incoming"), "shot.jpg");
        User user = new User("alice");

        Photo imported = Photo.importFile(src.toFile(), user);

        Path expectedDir = temp.resolve("users").resolve("alice").resolve("photos").toRealPath();
        assertTrue(Files.isDirectory(expectedDir));
        Path stored = Path.of(imported.getFilePath()).toRealPath();
        assertTrue(stored.startsWith(expectedDir), stored + " should be under " + expectedDir);
        assertTrue(Files.exists(stored));
        assertArrayEquals(Files.readAllBytes(src), Files.readAllBytes(stored));
    }
}
