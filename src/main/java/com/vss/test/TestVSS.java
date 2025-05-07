package main.java.com.vss.test;

import main.java.com.vss.model.Secret;
import main.java.com.vss.model.Share;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class TestVSS {
    public static void main(String[] args) {
        try {
            // Încărcarea imaginii secrete
            String inputPath = "/home/sebi/Pictures/profile_images/image_3.png"; // Înlocuiește cu calea către imaginea ta
            BufferedImage originalImage = ImageIO.read(new File(inputPath));

            BufferedImage image = ImageIO.read(new File(inputPath));
            BufferedImage rgbImage = new BufferedImage(
                    image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            rgbImage.getGraphics().drawImage(image, 0, 0, null);

            originalImage = rgbImage;

            // Crearea secretului
            Secret secret = new Secret(originalImage);
            secret.setImageMatrix(originalImage);

            // Setarea parametrilor
            int k = 3; // Număr minim de shares pentru reconstrucție
            int n = 5; // Număr total de shares

            secret.setMinimumNumberOfShares(k);
            secret.setTotalNumberOfShares(n);

            // Generarea share-urilor
            System.out.println("Generăm " + n + " shares...");
            List<Share> shares = secret.getShares(originalImage.getWidth(), originalImage.getHeight());

            // Salvarea share-urilor
            for (int i = 0; i < shares.size(); i++) {
                BufferedImage shareImage = shares.get(i).getShareImage(
                        originalImage.getWidth(), originalImage.getHeight());
                ImageIO.write(shareImage, "png", new File("test_output/share_" + (i+1) + ".png"));
                System.out.println("Share " + (i+1) + " salvat");
            }

            // Testăm reconstrucția cu diferite combinații de shares

            // Test 1: Folosim exact k share-uri
            System.out.println("\nTest 1: Reconstrucție cu exact " + k + " share-uri");
            List<Share> subset1 = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                subset1.add(shares.get(i));
            }

            BufferedImage reconstructed1 = secret.getSecretImage(
                    originalImage.getWidth(), originalImage.getHeight(), subset1);
            ImageIO.write(reconstructed1, "png", new File("test_output/reconstructed_test1.png"));

            // Test 2: Folosim toate share-urile
            System.out.println("\nTest 2: Reconstrucție cu toate cele " + n + " share-uri");
            BufferedImage reconstructed2 = secret.getSecretImage(
                    originalImage.getWidth(), originalImage.getHeight(), shares);
            ImageIO.write(reconstructed2, "png", new File("test_output/reconstructed_test2.png"));

            // Test 3: Folosim o combinație diferită de k share-uri
            System.out.println("\nTest 3: Reconstrucție cu o combinație diferită de " + k + " share-uri");
            List<Share> subset3 = new ArrayList<>();
            // Adăugăm ultimele k share-uri
            for (int i = n - k; i < n; i++) {
                subset3.add(shares.get(i));
            }

            BufferedImage reconstructed3 = secret.getSecretImage(
                    originalImage.getWidth(), originalImage.getHeight(), subset3);
            ImageIO.write(reconstructed3, "png", new File("test_output/reconstructed_test3.png"));

            System.out.println("\nTeste finalizate. Verificați imaginile rezultate.");

        } catch (IOException e) {
            System.err.println("Eroare la procesarea imaginii: " + e.getMessage());
            e.printStackTrace();
        }
    }
}