package main.java.com.vss.application.service;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import main.java.com.vss.model.Secret;
import main.java.com.vss.model.Share;
import main.java.com.vss.observer.ImageProcessingObserver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.CollationElementIterator;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class ShareService {
    private static ShareService instance;
    private static AuthService auth;
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
        this.auth = AuthService.getInstance();
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



    private String calculateSHA256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public boolean checkImageMatch(BufferedImage inputImage, int userId) {
        String sql = "select user_id, secret from secrets where user_id = ?";
        String logSql = "insert into activity_log (username, action, action_timestamp) values (?, ?, ?)";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             PreparedStatement logStmt = conn.prepareStatement(logSql)) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(inputImage, "png", baos);
            byte[] inputImageBytes = baos.toByteArray();
            String inputHash = calculateImageHash(inputImage);

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int imageId = rs.getInt("user_id");
                byte[] dbImageBytes = rs.getBytes("secret");

                logStmt.setString(1, auth.getUsername());
                logStmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));

                if (Arrays.equals(inputImageBytes, dbImageBytes)) {
                    logStmt.setString(2, "secret match success");
                    return true;
                }

                String dbHash = calculateImageHash(ImageIO.read(new ByteArrayInputStream(dbImageBytes)));
                if (inputHash.equals(dbHash)) {
                    logStmt.setString(2, "secret match success");
                    return true;
                }

            }

            logStmt.setString(2, "secret match failed");
            logStmt.executeUpdate();
            logStmt.close();

            return false;

        } catch (Exception e) {
            System.err.println("Eroare la verificarea imaginii: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String calculateImageHash(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return calculateSHA256(baos.toByteArray());
    }


    private int getUserIdByUsername() throws SQLException {
        String sql = "select id from users where username = ?";
        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, auth.getUsername());
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("User not found");
        }
    }

    public void decryptSecret() throws SQLException {
        createOrderedShareList();

        BufferedImage secretBuffered = secret.getSecretImage(this.width,this.height,shares,imageIndexes);
        Image secretImage = this.convertToFxImage(secretBuffered);

        System.out.println("Secret similarity: " + this.checkImageMatch(secretBuffered, getUserIdByUsername()) );

        List<Image> secretFx = new ArrayList<>();
        secretFx.add(secretImage);

        notifyProcessingCompleted(secretFx);

    }
}
