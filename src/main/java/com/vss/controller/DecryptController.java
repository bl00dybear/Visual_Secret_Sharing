package main.java.com.vss.controller;

import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.com.vss.application.service.ShareService;
import main.java.com.vss.observer.ImageProcessingObserver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DecryptController {
    private final ShareService shareService;

    public DecryptController() {
        this.shareService = ShareService.getInstance();
    }

    public void addObserver(ImageProcessingObserver observer){shareService.addObserver(observer);}

    private File chooseImageFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        return fileChooser.showOpenDialog(stage);
    }

    public void handleChooseImage(VBox root){
        File file = this.chooseImageFile((Stage) root.getScene().getWindow());

        if (file != null) {
            try {
                BufferedImage image = ImageIO.read(file);
                String fileName = file.getName();
                shareService.uploadImage(image,fileName);
            } catch (IOException e) {
                System.out.println("Failed to load image: " + e.getMessage());
            }
        }
    }

    public void handleDecryptImage(){
        shareService.decryptSecret();
    }

    public void handleClear(){}
}
