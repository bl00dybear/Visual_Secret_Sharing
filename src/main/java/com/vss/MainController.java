package main.java.com.vss;

import javafx.scene.image.Image;
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
    private App appReference;

    private boolean isAuthenticated = false;
    private String currentUser = null;

    private MainController() {
        this.service = SecretService.getInstance();
    }

    public static MainController getInstance() {
        if (controller == null) controller = new MainController();
        return controller;
    }

    public void setAppReference(App app) {
        this.appReference = app;
    }

    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                BufferedImage img = ImageIO.read(selectedFile);
                service.uploadImage(img);

                if (appReference != null) {
                    Image fxImage = new Image(selectedFile.toURI().toString());
                    appReference.updateImagePreview(fxImage);
                }
            } catch (IOException e) {
                System.err.println("Failed to load image: " + e.getMessage());
            }
        }
    }

    public boolean authenticate(String username, String password) {
        return Objects.equals(username, "admin") && Objects.equals(password, "admin");
    }

    public boolean createAccount(String username, String password) {
        return true; // mock logic
    }

    public void logout() {
        isAuthenticated = false;
        currentUser = null;
    }
}
