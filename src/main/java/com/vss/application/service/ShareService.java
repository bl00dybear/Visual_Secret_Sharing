package main.java.com.vss.application.service;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import main.java.com.vss.model.Secret;
import main.java.com.vss.model.Share;
import main.java.com.vss.observer.ImageProcessingObserver;

import java.awt.image.BufferedImage;
import java.text.CollationElementIterator;
import java.util.*;

public class ShareService {
    private static ShareService instance;
    private Secret secret;
    private Map<String,BufferedImage> images;
    private final List<Share> shares;
    private List<Integer> imageIndexes;
    private final List<ImageProcessingObserver> observers;

    private int width;
    private int height;

    private int minShares;

    private ShareService() {
        this.secret = new Secret(width,height);
        this.images = new TreeMap<>();
        this.shares = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.imageIndexes = new ArrayList<>();
    }

    public static synchronized ShareService getInstance() {
        if (instance == null) {
            instance = new ShareService();
        }
        return instance;
    }

    public void uploadImage(BufferedImage image, String name) {
        this.images.put(name,image);
        notifyImageLoaded(image);
    }

    public void addObserver(ImageProcessingObserver observer) {
        if(!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void setImageDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void notifyImageLoaded(BufferedImage image) {
        for (ImageProcessingObserver observer : observers) {
            observer.onImageLoaded(image);
        }

        setImageDimensions(image.getWidth(),image.getHeight());
    }

    public void notifyProcessingCompleted(List<Image> secret) {
        for (ImageProcessingObserver observer : observers) {
            observer.onProcessingCompleted(secret);
        }
    }

    private void createOrderedShareList(){
        for(String name : images.keySet()) {
            Share share = new Share(images.get(name));
            share.setImageMatrix(images.get(name));
            shares.add(share);

            if(name.startsWith("share_")) {
                int index = Integer.parseInt(name.substring(6,name.length()-4));
                imageIndexes.add(index);
            }
        }

        Collections.sort(imageIndexes);
    }

    private Image convertToFxImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public void decryptSecret(){
        createOrderedShareList();

        BufferedImage secretBuffered = secret.getSecretImage(this.width,this.height,shares,imageIndexes);
        Image secretImage = this.convertToFxImage(secretBuffered);

        List<Image> secretFx = new ArrayList<>();
        secretFx.add(secretImage);

        notifyProcessingCompleted(secretFx);

    }
}
