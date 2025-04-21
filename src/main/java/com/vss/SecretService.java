package main.java.com.vss;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;



public class SecretService {
    private BufferedImage image;
    private Secret secret;
    private List<Share> shares;

    private static SecretService instance;
    private SecretService() {
        shares = new ArrayList<>();
    }
    public static SecretService getInstance() {
        if (instance == null) {
            instance = new SecretService();
        }
        return instance;
    }



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


    public List<Image> processImage(Integer minimumNumberOfShares, Integer totalNumberOfShares) throws IOException {

        List<Image> fxImages = new ArrayList<>();

        if (image == null) {
            System.out.println("No image loaded!");
            return fxImages;
        }

        System.out.println("Processing image...");

        Secret secret = new Secret(image);
        secret.setImageMatrix(image);
        secret.setMinimumNumberOfShares(minimumNumberOfShares);
        secret.setTotalNumberOfShares(totalNumberOfShares);

        int width = image.getWidth();
        int height = image.getHeight();

        shares = secret.getShares(width, height);

        for(int i = 0; i < shares.size(); i++) {
//            shares.get(i).saveShareAsImage(i,"shares");
            BufferedImage shareImage = shares.get(i).getShareImage(width, height);
            fxImages.add(SwingFXUtils.toFXImage(shareImage, null));
        }

        return fxImages;
    }

}
