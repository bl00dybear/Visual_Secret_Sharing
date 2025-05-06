package main.java.com.vss.observer;

import javafx.scene.image.Image;
import main.java.com.vss.model.Share;

import java.awt.image.BufferedImage;
import java.util.List;

public interface ImageProcessingObserver {
    void onImageLoaded(BufferedImage image);

    void onProcessingStarted();

    void onProcessingCompleted(List<Image> shares);

    void onProcessingError(String errorMessage);
}
