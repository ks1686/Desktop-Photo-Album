package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;

/**
 * Tiny valid PNG bytes so Photo constructors do not depend on repo stock files.
 */
final class TestImages {

    // 1x1 transparent PNG
    private static final byte[] PNG = new byte[] {
        (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
        0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
        0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
        0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4,
        (byte) 0x89, 0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41,
        0x54, 0x78, (byte) 0x9C, 0x63, 0x00, 0x01, 0x00, 0x00,
        0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, (byte) 0xB4, 0x00,
        0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, (byte) 0xAE,
        0x42, 0x60, (byte) 0x82
    };

    private TestImages() {}

    static Path writePng(Path dir, String name) throws IOException {
        Path file = dir.resolve(name);
        Files.createDirectories(dir);
        Files.write(file, PNG);
        return file;
    }

    static long millis(int year, int monthZeroBased, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthZeroBased, day, hour, minute, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
