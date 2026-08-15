package model;

// Java imports
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user. A user has a username and a list of albums. A user can be
 * created with a username. A user can have albums added to it, deleted from it,
 * and retrieved from it. A user can be searched for photos based on tags. A
 * user can have its username changed.
 *
 * @author jacobjude
 */
public class User implements Serializable {
    private String username;
    private List<Album> albums; // ArrayList of albums
    private List<String> tagTypes;

    /**
     * Creates a user with the given username.
     *
     * @param username the username of the user
     * @throws NullPointerException if the username is null
     * @throws IllegalArgumentException if the username is empty
     */
    public User(String username) throws NullPointerException, IllegalArgumentException {
        if (username == null) {
            throw new NullPointerException("username cannot be null");
        } else if (username.isEmpty()) {
            throw new IllegalArgumentException("username cannot be empty");
        } 
            
        this.username = username;
        this.albums = new ArrayList<>(); // Initialize the ArrayList
        this.tagTypes = new ArrayList<>();
        // add some default tags like location, person, etc.
        this.tagTypes.add("Location");
        this.tagTypes.add("Person");
        this.tagTypes.add("Object");
    }

    /**
     * get the tag types
     * @return the tag types
     */
    public List<String> getTagTypes() {
        return this.tagTypes;
    }

    /**
     * get the username of the user
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * get the albums of the user
     * @return the albums of the user
     */
    public List<Album> getAlbums() {
        return albums;
    }

    /**
     * search for photos in the user's albums based on the given query
     * @param query: the query to search for
     * @return the photos that match the query
     * @throws IllegalArgumentException: if the query is null or empty
     */
    public List<Photo> searchAlbums(String query) throws IllegalArgumentException {
        List<Photo> photos = new ArrayList<>();
        for (Album album : albums) {
            for (Photo photo : album.search(query)) {
                if (!photos.contains(photo)) {
                    photos.add(photo);
                }
            }
        }
        return photos;
    }

    /**
     * create a new album with the given name
     * @param albumName: the name of the album to create
     * @return {@code true} if the album was added; {@code false} on null, blank, or duplicate
     */
    public boolean createAlbum(String albumName) {
        if (albumName == null || albumName.isBlank()) {
            return false;
        }
        for (Album a : albums) {
            if (a.getAlbumName().equals(albumName)) {
                return false;
            }
        }
        albums.add(new Album(albumName));
        return true;
    }

    /**
     * get the album with the given name
     * @param albumName: the name of the album to get
     * @return the album with the given name
     */
    public Album getAlbum(String albumName) {
        for (Album album : albums) {
            if (album.getAlbumName().equals(albumName)) {
                return album; // Return the album
            }
        }
        return null; // Return null if the album is not found
    }

    /**
     * toString method
     * @return the string representation of the user
     */
    public String toString() {
        String result = "";
        for (Album album : albums) {
            result += album.toString() + "\n";
        }

        return "User: " + username + "\n" + result;
    }


}
