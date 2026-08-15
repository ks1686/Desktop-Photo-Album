package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class UserAlbumTest {

    @TempDir
    Path temp;

    @Test
    void createAlbumDuplicateReturnsFalseAndLeavesSizeUnchanged() {
        User user = new User("alice");

        assertTrue(user.createAlbum("Vacation"));
        assertEquals(1, user.getAlbums().size());

        assertFalse(user.createAlbum("Vacation"));
        assertEquals(1, user.getAlbums().size());
        assertFalse(user.createAlbum("   "));
        assertFalse(user.createAlbum(null));
        assertEquals(1, user.getAlbums().size());
    }

    @Test
    void searchAlbumsNeverReturnsNullAndDedupesByPhotoEquals() throws Exception {
        Path file = TestImages.writePng(temp, "shared.png");
        Photo photo = new Photo(file.toAbsolutePath().toString());
        photo.addTag("person", "Ada");

        User user = new User("alice");
        assertTrue(user.createAlbum("one"));
        assertTrue(user.createAlbum("two"));
        user.getAlbum("one").addPhoto(photo);
        user.getAlbum("two").addPhoto(photo);

        List<Photo> hits = user.searchAlbums("person=Ada");
        assertNotNull(hits);
        assertEquals(1, hits.size());
        assertEquals(photo, hits.getFirst());

        List<Photo> empty = user.searchAlbums("person=Nobody");
        assertNotNull(empty);
        assertTrue(empty.isEmpty());
    }

    @Test
    void searchAlbumsThrowsOnInvalidQuery() {
        User user = new User("alice");
        user.createAlbum("one");

        assertThrows(IllegalArgumentException.class, () -> user.searchAlbums("not-a-query"));
    }
}
