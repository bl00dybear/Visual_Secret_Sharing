package main.java.com.vss;


import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainController {

    private static MainController controller;
    private final SecretService service;

    private boolean isAuthenticated = false;
    private String currentUser = null;


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

    public boolean authenticate(String username, String password) {
        return Objects.equals(username, "admin") && Objects.equals(password, "admin");
    }

    public boolean createAccount(String username, String password) {
        if (false) {
            return false;
        }

        return true;
    }

    public void logout() {
        isAuthenticated = false;
        currentUser = null;
    }
}
