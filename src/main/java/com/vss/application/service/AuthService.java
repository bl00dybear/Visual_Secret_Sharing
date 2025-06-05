package main.java.com.vss.application.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    private static AuthService instance;
    private String username;
    

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public String getUsername() {
        return this.username;
    }

    private static String hashPasswordSHA256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }


    public boolean authenticate(String username, String password) {
        String authSql = "select * from users where username = ? and password = ?";
        String logSql = "insert into activity_log (username, action, action_timestamp) values (?, ?, ?)";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement verifyStmt = conn.prepareStatement(authSql);
            PreparedStatement logStmt = conn.prepareStatement(logSql);){

            String hashedPassword = hashPasswordSHA256(password);

            verifyStmt.setString(1, username);
            verifyStmt.setString(2, hashedPassword);

//            System.out.println("A ajuns aici");

            ResultSet rs = verifyStmt.executeQuery();

            logStmt.setString(1, username);
            logStmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));

            boolean authenticated = rs.next();

            if(authenticated) {
                logStmt.setString(2, "login successfully");
                this.username = username;
            }else {
                logStmt.setString(2, "login failed");
            }

            logStmt.executeUpdate();
            logStmt.close();

            return authenticated;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createAccount(String username, String password) {
        String checkSql = "SELECT 1 FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";
        String logSql = "insert into activity_log (username, action, action_timestamp) values (?, ?, ?)";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement logStmt = conn.prepareStatement(logSql);){

            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false;
            }

            String hashedPassword = hashPasswordSHA256(password);

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, hashedPassword);
                insertStmt.executeUpdate();

                logStmt.setString(1, username);
                logStmt.setString(2, "account created");
                logStmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
                logStmt.executeUpdate();

                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void deleteAccount() {
        String deleteSql = "DELETE FROM users WHERE username = ?";
        String logSql = "insert into activity_log (username, action, action_timestamp) values (?, ?, ?)";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
             PreparedStatement logStmt = conn.prepareStatement(logSql)) {

            deleteStmt.setString(1, this.username);
            deleteStmt.executeUpdate();

            logStmt.setString(1, this.username);
            logStmt.setString(2, "account deleted");
            logStmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            logStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updatePassword(String oldPassword, String newPassword) {
    String checkSql = "SELECT password FROM users WHERE username = ?";
    String updateSql = "UPDATE users SET password = ? WHERE username = ?";
    String logSql = "INSERT INTO activity_log (username, action, action_timestamp) VALUES (?, ?, ?)";

    try (Connection conn = DatabaseService.getConnection();
         PreparedStatement checkStmt = conn.prepareStatement(checkSql);
         PreparedStatement updateStmt = conn.prepareStatement(updateSql);
         PreparedStatement logStmt = conn.prepareStatement(logSql)) {

        checkStmt.setString(1, this.username);
        ResultSet rs = checkStmt.executeQuery();
        
        if (!rs.next() || !rs.getString("password").equals(hashPasswordSHA256(oldPassword))) {
            logStmt.setString(1, this.username);
            logStmt.setString(2, "password update failed - wrong old password");
            logStmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            logStmt.executeUpdate();
            return false;
        }

        String hashedNewPassword = hashPasswordSHA256(newPassword);
        updateStmt.setString(1, hashedNewPassword);
        updateStmt.setString(2, this.username);
        updateStmt.executeUpdate();

        logStmt.setString(1, this.username);
        logStmt.setString(2, "password updated successfully");
        logStmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
        logStmt.executeUpdate();

        return true;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

}
