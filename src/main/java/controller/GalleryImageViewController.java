package controller;

import java.io.File;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Album;
import model.Photo;

/**
 * Thumbnail tile pane for an album.
 */
public class GalleryImageViewController {

    @FXML private TilePane galleryImageView;
    private Photo selectedPhoto;
    private VBox selectedContainer;

    public Photo getSelectedPhoto() {
        return this.selectedPhoto;
    }

    protected void addToGallery(Photo photo) {
        String filepath = photo.getFilePath();
        Image image = new Image(new File(filepath).toURI().toString(), 150, 150, true, true);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        Text caption = new Text(photo.getCaption());
        caption.setWrappingWidth(150);
        caption.setStyle("-fx-font-size: 10px;");
        caption.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox container = new VBox(imageView, caption);
        container.setAlignment(javafx.geometry.Pos.CENTER);
        container.setSpacing(5);
        container.setOnMouseClicked(e -> select(photo, container));
        galleryImageView.getChildren().add(container);
    }

    private void select(Photo photo, VBox container) {
        if (selectedContainer != null) {
            selectedContainer.setStyle("");
        }
        selectedPhoto = photo;
        selectedContainer = container;
        container.setStyle("-fx-border-color: #3366cc; -fx-border-width: 2; -fx-padding: 2;");
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
