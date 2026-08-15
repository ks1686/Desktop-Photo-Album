package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AlbumSearchTest {

    @TempDir
    Path temp;

    @Test
    void dateRangeUsesOneBasedMonthNotCalendarMonth() throws Exception {
        Path marchFile = TestImages.writePng(temp, "march.png");
        marchFile.toFile().setLastModified(TestImages.millis(2024, Calendar.MARCH, 15, 15, 30));
        Photo march = new Photo(marchFile.toAbsolutePath().toString());

        Path aprilFile = TestImages.writePng(temp, "april.png");
        aprilFile.toFile().setLastModified(TestImages.millis(2024, Calendar.APRIL, 15, 10, 0));
        Photo april = new Photo(aprilFile.toAbsolutePath().toString());

        Album album = new Album("dates", List.of(march, april));

        List<Photo> hits = album.search("03/15/2024-03/15/2024");

        assertEquals(1, hits.size(), "March 15 query must not shift to April (month off-by-one)");
        assertEquals(march, hits.getFirst());
        assertEquals(Calendar.MARCH, march.getDate().get(Calendar.MONTH));
        assertEquals(0, march.getDate().get(Calendar.HOUR_OF_DAY));
    }

    @Test
    void invalidConjunctionTagThrowsIllegalArgumentExceptionNotAioobe() throws Exception {
        Path file = TestImages.writePng(temp, "tagged.png");
        Photo photo = new Photo(file.toAbsolutePath().toString());
        photo.addTag("person", "Ada");
        Album album = new Album("tags", List.of(photo));

        assertThrows(IllegalArgumentException.class, () -> album.search("person AND location"));
    }

    @Test
    void singleTagAllowsSpacesInValue() throws Exception {
        Path file = TestImages.writePng(temp, "nyc.png");
        Photo photo = new Photo(file.toAbsolutePath().toString());
        photo.addTag("location", "New York");
        Album album = new Album("tags", List.of(photo));

        List<Photo> hits = album.search("location=New York");

        assertEquals(List.of(photo), hits);
    }

    @Test
    void andOrMatchValueForKeyNotContainsValue() throws Exception {
        Path file = TestImages.writePng(temp, "pair.png");
        Photo photo = new Photo(file.toAbsolutePath().toString());
        photo.addTag("person", "Ada");
        photo.addTag("location", "Paris");
        Album album = new Album("tags", List.of(photo));

        assertEquals(List.of(photo), album.search("person=Ada AND location=Paris"));
        assertTrue(album.search("person=Paris AND location=Ada").isEmpty());
        assertEquals(List.of(photo), album.search("person=Ada OR location=London"));
    }
}
