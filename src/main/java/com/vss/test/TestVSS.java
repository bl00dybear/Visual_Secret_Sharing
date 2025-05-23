package main.java.com.vss.test;

import main.java.com.vss.model.Secret;
import main.java.com.vss.model.Share;

import javax.imageio.ImageIO;
import java.awt.desktop.SystemSleepEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class TestVSS {
    public static void main(String[] args) {
        try {
            // Încărcarea imaginii secrete
            String inputPath = "/home/sebi/Pictures/profile_images/test.png"; // Înlocuiește cu calea către imaginea ta
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

//            25 44 117 60

            for(int i = 0; i < originalImage.getWidth(); i++) {
                for(int j = 0; j < originalImage.getHeight(); j++) {
                    int rgb = originalImage.getRGB(j, i);
                    int red = (rgb >> 16) & 0xff;
                    int green = (rgb >> 8) & 0xff;
                    int blue = rgb & 0xff;

                    System.out.println("RGB: " + red + " " + green + " " + blue);
                }
            }

            System.out.println("Printare cu metoda\n");

            secret.printImageStats();


            // Generarea share-urilor
            System.out.println("Generăm " + n + " shares...");
            List<Share> shares = secret.getShares(originalImage.getWidth(), originalImage.getHeight());

            for(Share share : shares) {
                share.printImageStats();
                System.out.println();
            }

            // Salvarea share-urilor
            for (int i = 0; i < shares.size(); i++) {
                BufferedImage shareImage = shares.get(i).getShareImage(
                        originalImage.getWidth(), originalImage.getHeight());
                ImageIO.write(shareImage, "png", new File("test_output/share_" + (i+1) + ".png"));
                System.out.println("Share " + (i+1) + " salvat");
            }


            // Test : Folosim exact k share-uri
            System.out.println("\nTest : Reconstrucție cu exact " + k + " share-uri");
            List<Share> subset1 = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                subset1.add(shares.get(i));
            }

//            BufferedImage reconstructed1 = secret.getSecretImage(
//                    originalImage.getWidth(), originalImage.getHeight(), subset1);
//            ImageIO.write(reconstructed1, "png", new File("test_output/reconstructed_test1.png"));

            secret.printImageStats();


        } catch (IOException e) {
            System.err.println("Eroare la procesarea imaginii: " + e.getMessage());
            e.printStackTrace();
        }
    }
}