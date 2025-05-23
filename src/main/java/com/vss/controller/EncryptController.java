package main.java.com.vss.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.com.vss.application.service.SecretService;
import main.java.com.vss.observer.ImageProcessingObserver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.scene.image.Image;

public class EncryptController {
    private final SecretService secretService;

    public EncryptController() {
        this.secretService = SecretService.getInstance();
    }

    public void addObserver(ImageProcessingObserver observer){
        secretService.addObserver(observer);
    }

    private File chooseImageFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        return fileChooser.showOpenDialog(stage);
    }

    public void handleProcessImage(int totalShares, int minShares) {
        secretService.createShares(minShares, totalShares);
    }

    public void handleChooseImage(VBox root){
        File file = this.chooseImageFile((Stage) root.getScene().getWindow());

        if (file != null) {
            try {
                BufferedImage image = ImageIO.read(file);
                secretService.uploadImage(image);
            } catch (IOException e) {
                System.out.println("Failed to load image: " + e.getMessage());
            }
        }
    }

    public void handleSaveShares(List<Image> shares, String outputDir){
        File dir = new File(outputDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println("Fail to create directory: " + outputDir);
            }
        }

        for(Image share : shares) {
            String fileName = outputDir + File.separator + "share_" + (shares.indexOf(share)+1) + ".png";
            File outputFile = new File(fileName);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(share, null), "png", outputFile);
            } catch (IOException e) {
                System.out.println("Failed to save image: " + e.getMessage());
            }
            System.out.println("Image saved to: " + outputFile.getAbsolutePath());
        }
    }

    public void handleClear(){}

}
