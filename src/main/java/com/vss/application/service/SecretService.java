package main.java.com.vss.application.service;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import main.java.com.vss.model.Share;
import main.java.com.vss.model.Secret;
import main.java.com.vss.observer.ImageProcessingObserver;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SecretService {
    private static SecretService instance;
    private BufferedImage image;
    private Secret secret;
    private List<Share> shares;
    private final List<ImageProcessingObserver> observers = new ArrayList<>();

    private  SecretService()  {
        shares = new ArrayList<>();
    }

    public static synchronized SecretService getInstance() {
        if (instance == null) {
            instance = new SecretService();
        }
        return instance;
    }

    public void addObserver(ImageProcessingObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    private void notifyImageLoaded(BufferedImage image) {
        for (ImageProcessingObserver observer : observers) {
            observer.onImageLoaded(image);
        }

        this.image = image;
    }

    private void notifyProcessingCompleted(List<Image> shares) {
        for (ImageProcessingObserver observer : observers) {
            observer.onProcessingCompleted(shares);
        }
    }

    public void uploadImage(BufferedImage image) {
        this.image = image;
        notifyImageLoaded(image);
    }

    private Image convertToFxImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public void createShares(int minShares, int totalShares) {
        this.secret = new Secret(image);

        secret.setImageMatrix(image);
        secret.setMinimumNumberOfShares(minShares);
        secret.setTotalNumberOfShares(totalShares);

        int width = image.getWidth();
        int height = image.getHeight();

        shares = secret.getShares(width, height);

        List<BufferedImage> sharesBuffered = new ArrayList<>();

        for (Share share : shares) {
            sharesBuffered.add(share.getShareImage(width, height));
        }

        List<Image> sharesFx = new ArrayList<>();

        for(BufferedImage bufferedImage : sharesBuffered) {
            sharesFx.add(convertToFxImage(bufferedImage));
        }

        notifyProcessingCompleted(sharesFx);

    }
}
