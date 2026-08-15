package model;

// Java imports
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Represents an album of photos. An album has a name and a list of photos. An
 * album can be created with a name and a list of photos. An album can have
 * photos added to it, removed from it, and moved to another album. An album can
 * be searched for photos based on tags or dates. An album can have its name
 * changed. An album can have its start and end dates retrieved.
 *
 * @author jacobjude
 */
public class Album implements Serializable {
    private String albumName;
    private List<Photo> photos;

    /**
     * Creates an album with the given name and an empty list of photos.
     *
     * @param albumName the name of the album
     * @throws NullPointerException     if albumName is null
     * @throws IllegalArgumentException if albumName is empty
     */
    public Album(String albumName) throws NullPointerException, IllegalArgumentException {
        this(albumName, new ArrayList<>());
    }

    /**
     * Creates an album with the given name and list of photos.
     *
     * @param albumName the name of the album
     * @param photos    the list of photos in the album
     * @throws NullPointerException     if albumName or photos is null
     * @throws IllegalArgumentException if albumName is empty
     */
    public Album(String albumName, List<Photo> photos) throws NullPointerException, IllegalArgumentException {
        if (albumName == null) {
            throw new NullPointerException("albumName cannot be null");
        } else if (albumName.isEmpty()) {
            throw new IllegalArgumentException("albumName cannot be empty");
        }
        if (photos == null) {
            throw new NullPointerException("photos cannot be null");
        }

        this.albumName = albumName;
        this.photos = photos;
    }

    /**
     * Adds a photo to the album.
     *
     * @param photo the photo to add
     */
    public void addPhoto(Photo photo) {
        this.photos.add(photo); // may need to catch an exception here?
    }

    /**
     * Adds a photo to the album.
     *
     * @param filepath the filepath of the photo to add
     */
    public void addPhoto(String filepath) {
        this.photos.add(new Photo(filepath)); // may need to catch an exception here?
    }

    /**
     * Removes a photo from the album.
     *
     * @param photo the photo to remove
     */
    public void removePhoto(Photo photo) {
        this.photos.remove(photo);
    }

    /**
     * Gets a photo from the album.
     *
     * @return arrayList of photos
     */
    public List<Photo> getPhotos() {
        return new ArrayList<>(this.photos);
    }

    /**
     * get the name of the album
     *
     * @return the name of the album
     */
    public String getAlbumName() {
        return albumName;
    }

    /**
     * get the size of the album
     *
     * @return the size of the album
     */
    public int getSize() {
        return this.photos.size();
    }

    /**
     * set the name of the album
     *
     * @param albumName the name of the album
     * @throws NullPointerException     if albumName is null
     * @throws IllegalArgumentException if albumName is empty
     */
    public void setAlbumName(String albumName) throws NullPointerException, IllegalArgumentException {
        if (albumName == null) {
            throw new NullPointerException("albumName cannot be null");
        } else if (albumName.isEmpty()) {
            throw new IllegalArgumentException("albumName cannot be empty");
        }
        this.albumName = albumName;
    }

    /**
     * toString method for the album
     *
     * @return the string representation of the album
     */
    public String toString() {
        // get the toString of all the photos in the album and album name
        String result = "";
        for (Photo photo : this.photos) {
            result += photo.toString() + "\n";
        }
        return "Album: " + this.albumName + "\nPhotos:\n" + result;
    }

    /**
     * get the start date of the album
     *
     * @return the start date of the album
     */
    public Calendar getStartDate() {
        if (this.photos.isEmpty()) {
            return null;
        }
        Calendar startDate = this.photos.getFirst().getDate();
        for (Photo photo : this.photos) {
            if (photo.getDate().compareTo(startDate) < 0) {
                startDate = photo.getDate();
            }
        }
        return startDate;
    }

    /**
     * get the end date of the album
     *
     * @return the end date of the album
     */
    public Calendar getEndDate() {
        if (this.photos.isEmpty()) {
            return null;
        }
        Calendar endDate = this.photos.getFirst().getDate();
        for (Photo photo : this.photos) {
            if (photo.getDate().compareTo(endDate) > 0) {
                endDate = photo.getDate();
            }
        }
        return endDate;
    }

    /**
     * search for photos in the album based on a query
     *
     * @param query the query to search for
     * @return the list of photos that match the query
     * @throws IllegalArgumentException if the query is invalid
     */
    public List<Photo> search(String query) {
        return PhotoSearch.search(this.photos, query);
    }

}