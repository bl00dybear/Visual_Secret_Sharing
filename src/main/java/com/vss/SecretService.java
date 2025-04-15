package main.java.com.vss;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

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
    private Secret secret;

    public static Boolean minAndTotalNumberOfShares = false;

    private BufferedImage convertToGrayscale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        ColorConvertOp op = new ColorConvertOp(
                image.getColorModel().getColorSpace(),
                newImage.getColorModel().getColorSpace(),
                null
        );
        return op.filter(image, newImage);
    }

    public Boolean isImageUploaded(){
        return this.image != null;
    }


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

//        image = this.convertToGrayscale(image);

        Secret secret = new Secret(image);


    }

}
