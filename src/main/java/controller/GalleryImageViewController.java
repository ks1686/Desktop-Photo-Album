package controller;

import java.io.File;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Album;
import model.Photo;

/**
 * Thumbnail tile pane for an album. Tiles use the shared .photo-tile style;
 * selection is tracked by tile, not by parsing display text.
 */
public class GalleryImageViewController {

    private static final int THUMB_SIZE = 170;

    @FXML private TilePane galleryImageView;
    private Photo selectedPhoto;
    private VBox selectedContainer;

    public Photo getSelectedPhoto() {
        return this.selectedPhoto;
    }

    protected void addToGallery(Photo photo) {
        String filepath = photo.getFilePath();
        Image image = new Image(new File(filepath).toURI().toString(), THUMB_SIZE, THUMB_SIZE, true, true);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(THUMB_SIZE);
        imageView.setFitHeight(THUMB_SIZE);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        Text caption = new Text(photo.getCaption() == null ? "" : photo.getCaption());
        caption.setWrappingWidth(THUMB_SIZE);
        caption.getStyleClass().add("photo-caption");

        VBox container = new VBox(imageView, caption);
        container.setAlignment(Pos.CENTER);
        container.setSpacing(6);
        container.getStyleClass().add("photo-tile");
        container.setOnMouseClicked(e -> select(photo, container));
        galleryImageView.getChildren().add(container);
    }

    private void select(Photo photo, VBox container) {
        if (selectedContainer != null) {
            selectedContainer.getStyleClass().remove("selected");
            selectedContainer.getStyleClass().add("photo-tile");
        }
        selectedPhoto = photo;
        selectedContainer = container;
        container.getStyleClass().remove("photo-tile");
        container.getStyleClass().add("selected");
    }

    public void start(Album album) {
        galleryImageView.getChildren().clear();
        selectedPhoto = null;
        selectedContainer = null;
        List<Photo> photos = album.getPhotos();
        for (Photo photo : photos) {
            addToGallery(photo);
        }
    }

    public TilePane getGalleryImageView() {
        return galleryImageView;
    }
}
