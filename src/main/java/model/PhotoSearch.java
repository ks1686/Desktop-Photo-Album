package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Parses album search queries: a date range, a single tag, or AND/OR of two tags.
 */
public final class PhotoSearch {

    private PhotoSearch() {}

    /**
     * @return photos matching {@code query}; never {@code null}
     * @throws IllegalArgumentException if the query is null, blank, or malformed
     */
    public static List<Photo> search(List<Photo> photos, String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Invalid query");
        }
        if (query.matches("\\d{2}/\\d{2}/\\d{4}-\\d{2}/\\d{2}/\\d{4}")) {
            return searchDateRange(photos, query);
        }
        return searchTags(photos, query);
    }

    private static List<Photo> searchDateRange(List<Photo> photos, String query) {
        String[] dates = query.split("-");
        Calendar start = parseDate(dates[0], 0, 0, 0, 0);
        Calendar end = parseDate(dates[1], 23, 59, 59, 0);
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Invalid query");
        }
        List<Photo> result = new ArrayList<>();
        for (Photo photo : photos) {
            Calendar date = photo.getDate();
            if (date.compareTo(start) >= 0 && date.compareTo(end) <= 0) {
                result.add(photo);
            }
        }
        return result;
    }

    private static Calendar parseDate(String mmddyyyy, int hour, int minute, int second, int millis) {
        String[] parts = mmddyyyy.split("/");
        int month = Integer.parseInt(parts[0]);
        int day = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millis);
        return calendar;
    }

    private static List<Photo> searchTags(List<Photo> photos, String query) {
        String[] parts = query.split(" AND | OR ");
        if (parts.length == 1) {
            String[] tag = parseTag(parts[0]);
            return filter(photos, photo -> matches(photo, tag[0], tag[1]));
        }
        if (parts.length == 2) {
            String[] tag1 = parseTag(parts[0]);
            String[] tag2 = parseTag(parts[1]);
            if (query.contains(" AND ")) {
                return filter(photos, photo -> matches(photo, tag1[0], tag1[1]) && matches(photo, tag2[0], tag2[1]));
            }
            if (query.contains(" OR ")) {
                return filter(photos, photo -> matches(photo, tag1[0], tag1[1]) || matches(photo, tag2[0], tag2[1]));
            }
        }
        throw new IllegalArgumentException("Invalid query");
    }

    /**
     * Splits {@code key=value}. Spaces are allowed in the value.
     */
    private static String[] parseTag(String part) {
        if (part == null || !part.contains("=")) {
            throw new IllegalArgumentException("Invalid query");
        }
        String[] tag = part.split("=", 2);
        if (tag.length != 2 || tag[0].isEmpty() || tag[1].isEmpty()) {
            throw new IllegalArgumentException("Invalid query");
        }
        return tag;
    }

    private static boolean matches(Photo photo, String key, String value) {
        for (Map<String, String> tag : photo.getTags()) {
            if (value.equals(tag.get(key))) {
                return true;
            }
        }
        return false;
    }

    private static List<Photo> filter(List<Photo> photos, java.util.function.Predicate<Photo> predicate) {
        List<Photo> result = new ArrayList<>();
        for (Photo photo : photos) {
            if (predicate.test(photo)) {
                result.add(photo);
            }
        }
        return result;
    }
}
