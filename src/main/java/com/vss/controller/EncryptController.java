package main.java.com.vss.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.com.vss.application.service.AuthService;
import main.java.com.vss.application.service.DatabaseService;
import main.java.com.vss.application.service.SecretService;
import main.java.com.vss.observer.ImageProcessingObserver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javafx.scene.image.Image;
import main.java.com.vss.view.InterfaceManager;

public class EncryptController {
    private final SecretService secretService;
    private final InterfaceManager interfaceManager;
    private final AuthService authService;

    public EncryptController(InterfaceManager interfaceManager) {
        this.interfaceManager = interfaceManager;
        this.secretService = SecretService.getInstance();
        this.authService = AuthService.getInstance();
    }

    public void addObserver(ImageProcessingObserver observer){
        secretService.addObserver(observer);
    }

    private File chooseImageFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        return fileChooser.showOpenDialog(stage);
    }

    private int getUserIdByUsername(Connection conn, String username) throws SQLException {
        String sql = "select id from users where username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("User not found");
        }
    }

    private void saveSecretInDB() {
        String insertSql = "insert into secrets (user_id, secret) values (?, ?)";
        String logSql = "insert into activity_log (username, action, action_timestamp) values (?, ?, ?)";

        try (Connection conn = DatabaseService.getConnection()) {
            String username = authService.getUsername();
            int userId = getUserIdByUsername(conn, username);

            try (PreparedStatement pstmt = conn.prepareStatement(insertSql);
                 PreparedStatement logStmt = conn.prepareStatement(logSql)) {

                pstmt.setInt(1, userId);
                pstmt.setBytes(2, secretService.getSecretBytes());
                pstmt.executeUpdate();

                logStmt.setString(1, username);
                logStmt.setString(2, "secret saved");
                logStmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
                logStmt.executeUpdate();

            }
        } catch (Exception e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();

            try (Connection conn = DatabaseService.getConnection();
                 PreparedStatement logStmt = conn.prepareStatement(logSql)) {

                logStmt.setString(1, authService.getUsername());
                logStmt.setString(2, "failed to save secret: ");
                logStmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
                logStmt.executeUpdate();

            } catch (SQLException logEx) {
                System.out.println("Failed to log error: " + logEx.getMessage());
            }
        }
    }

    public void handleProcessImage(int totalShares, int minShares) {
        secretService.createShares(minShares, totalShares);
    }

    public void handleChooseImage(VBox root){
        File file = this.chooseImageFile((Stage) root.getScene().getWindow());

        if (file != null) {
            try {
                BufferedImage image = ImageIO.read(file);
                secretService.uploadImage(image,file.getName());
            } catch (IOException e) {
                System.out.println("Failed to load image: " + e.getMessage());
            }
        }
    }

    public void handleSaveShares(List<Image> shares, String outputDir){
        File dir = new File(outputDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println("Fail to create directory: " + outputDir);
            }
        }

        for(Image share : shares) {
            String fileName = outputDir + File.separator + "share_" + (shares.indexOf(share)+1) + ".png";
            File outputFile = new File(fileName);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(share, null), "png", outputFile);
            } catch (IOException e) {
                System.out.println("Failed to save image: " + e.getMessage());
            }
            System.out.println("Image saved to: " + outputFile.getAbsolutePath());
        }

        saveSecretInDB();
    }

    public void handleDecrypt() {
        System.out.println("Decrypting...");
        interfaceManager.showDecryptScreen();
    }

    public void handleClear(){}

}
