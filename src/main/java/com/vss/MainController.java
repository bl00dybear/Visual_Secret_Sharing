package main.java.com.vss;


import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainController {

    private static MainController controller;
    private final SecretService service;


    public MainController() {
        this.service = SecretService.getInstance();
    }

    public static MainController getInstance() {
        if (controller == null) {
            controller = new MainController();
        }
        return controller;
    }
    public void chooseFile() {
        System.out.println("Choosing file...");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                BufferedImage image = ImageIO.read(selectedFile);
                System.out.println("Image selected: " + selectedFile.getName());

                service.uploadImage(image);  // delegare către serviciu

            } catch (IOException e) {
                System.err.println("Failed to load image: " + e.getMessage());
            }
        } else {
            System.out.println("No file selected.");
        }

    }
}
