package main.java.com.vss.controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.com.vss.application.service.SecretService;
import main.java.com.vss.observer.ImageProcessingObserver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EncryptController {
    private final SecretService secretService;

    public EncryptController() {
        this.secretService = SecretService.getInstance();
    }

    public void addObserver(ImageProcessingObserver observer){
        secretService.addObserver(observer);
    }

    public File chooseImageFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        return fileChooser.showOpenDialog(stage);
    }

    public void loadImage(File file) throws IOException {
        if (file != null) {
            BufferedImage image = ImageIO.read(file);
            secretService.uploadImage(image);
            System.out.println("Aici");
        }
    }

    public void encryptImage(int totalShares, int minShares) {
        secretService.createShares(minShares, totalShares);
    }
}
