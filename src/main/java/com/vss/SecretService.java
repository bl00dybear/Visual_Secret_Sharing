package main.java.com.vss;

import java.awt.image.BufferedImage;

public class SecretService {
    private static SecretService instance;
    private SecretService() {}
    public static SecretService getInstance() {
        if (instance == null) {
            instance = new SecretService();
        }
        return instance;
    }

    private BufferedImage image;

    public void uploadImage(BufferedImage image) {
        this.image = image;
        System.out.println("Image loaded.");
    }

    public void processImage() {
        if (image == null) {
            System.out.println("No image loaded!");
            return;
        }

        System.out.println("Processing image...");

    }

}
