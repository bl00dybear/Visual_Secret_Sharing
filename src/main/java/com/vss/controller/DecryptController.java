package main.java.com.vss.controller;

import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.com.vss.application.service.ShareService;
import main.java.com.vss.observer.ImageProcessingObserver;
import main.java.com.vss.view.InterfaceManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class DecryptController {
    private final ShareService shareService;
    private final InterfaceManager interfaceManager;

    public DecryptController(InterfaceManager interfaceManager) {
        this.shareService = ShareService.getInstance();
        this.interfaceManager = interfaceManager;
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

    public void handleDecryptImage() throws SQLException {
        shareService.decryptSecret();
    }

    public void handleEncrypt() {
        System.out.println("Encrypting...");
        interfaceManager.showEncryptScreen();
    }

    public void handleClear(){}
}
