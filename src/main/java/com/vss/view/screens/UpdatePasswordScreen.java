package main.java.com.vss.view.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.java.com.vss.controller.AuthController;
import main.java.com.vss.view.InterfaceManager;
import main.java.com.vss.view.components.UIComponents;

public class UpdatePasswordScreen {
    private final InterfaceManager interfaceManager;
    private final AuthController authController;
    private final VBox root;

    private PasswordField oldPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;

    private static final Text asciiText = new Text("""
                
                  ██████ ▓█████  ▄████▄   ██▀███  ▓█████▄▄▄█████▓     ██████  ██░ ██  ▄▄▄       ██▀███   ██▓ ███▄    █   ▄████ 
                ▒██    ▒ ▓█   ▀ ▒██▀ ▀█  ▓██ ▒ ██▒▓█   ▀▓  ██▒ ▓▒   ▒██    ▒ ▓██░ ██▒▒████▄    ▓██ ▒ ██▒▓██▒ ██ ▀█   █  ██▒ ▀█▒
                ░ ▓██▄   ▒███   ▒▓█    ▄ ▓██ ░▄█ ▒▒███  ▒ ▓██░ ▒░   ░ ▓██▄   ▒██▀▀██░▒██  ▀█▄  ▓██ ░▄█ ▒▒██▒▓██  ▀█ ██▒▒██░▄▄▄░
                  ▒   ██▒▒▓█  ▄ ▒▓▓▄ ▄██▒▒██▀▀█▄  ▒▓█  ▄░ ▓██▓ ░      ▒   ██▒░▓█ ░██ ░██▄▄▄▄██ ▒██▀▀█▄  ░██░▓██▒  ▐▌██▒░▓█  ██▓
                ▒██████▒▒░▒████▒▒ ▓███▀ ░░██▓ ▒██▒░▒████▒ ▒██▒ ░    ▒██████▒▒░▓█▒░██▓ ▓█   ▓██▒░██▓ ▒██▒░██░▒██░   ▓██░░▒▓███▀▒
                ▒ ▒▓▒ ▒ ░░░ ▒░ ░░ ░▒ ▒  ░░ ▒▓ ░▒▓░░░ ▒░ ░ ▒ ░░      ▒ ▒▓▒ ▒ ░ ▒ ░░▒░▒ ▒▒   ▓▒█░░ ▒▓ ░▒▓░░▓  ░ ▒░   ▒ ▒  ░▒   ▒ 
                ░ ░▒  ░ ░ ░ ░  ░  ░  ▒     ░▒ ░ ▒░ ░ ░  ░   ░       ░ ░▒  ░ ░ ▒ ░▒░ ░  ▒   ▒▒ ░  ░▒ ░ ▒░ ▒ ░░ ░░   ░ ▒░  ░   ░ 
                ░  ░  ░     ░   ░          ░░   ░    ░    ░         ░  ░  ░   ░  ░░ ░  ░   ▒     ░░   ░  ▒ ░   ░   ░ ░ ░ ░   ░ 
                      ░     ░  ░░ ░         ░        ░  ░                 ░   ░  ░  ░      ░  ░   ░      ░           ░       ░ 
                                ░                                                                                              
                """);

    public UpdatePasswordScreen(InterfaceManager interfaceManager, AuthController authController) {
        this.interfaceManager = interfaceManager;
        this.authController = authController;

        root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        root.getChildren().addAll(
                createTitle(),
                createUpdatePasswordBox()
        );
    }

    public VBox getRoot() {
        return root;
    }

    private Text createTitle() {
        asciiText.setFont(Font.font("Monospaced", 14));
        return asciiText;
    }

    private StackPane createUpdatePasswordBox() {
        oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("Current Password");
        oldPasswordField.setFont(Font.font("DejaVu Sans Mono", 14));
        oldPasswordField.setPrefWidth(300);

        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        newPasswordField.setFont(Font.font("DejaVu Sans Mono", 14));
        newPasswordField.setPrefWidth(300);

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");
        confirmPasswordField.setFont(Font.font("DejaVu Sans Mono", 14));
        confirmPasswordField.setPrefWidth(300);

        Button updateButton = UIComponents.createButton("Update Password", event -> handleUpdatePassword());
        Button backButton = UIComponents.createButton("Back", event -> interfaceManager.showMainScreen());

        HBox buttonContainer = new HBox(20, updateButton, backButton);
        buttonContainer.setAlignment(Pos.CENTER);

        Label title = new Label("UPDATE PASSWORD");
        title.setFont(Font.font("DejaVu Sans Mono", 24));
        title.setTextFill(Color.web("#2c3e50"));

        VBox updateForm = new VBox(15);
        updateForm.setAlignment(Pos.CENTER);
        updateForm.setPadding(new Insets(20));
        updateForm.getChildren().addAll(
                title,
                UIComponents.createLabeledInput("Current Password:", oldPasswordField),
                UIComponents.createLabeledInput("New Password:", newPasswordField),
                UIComponents.createLabeledInput("Confirm Password:", confirmPasswordField),
                buttonContainer
        );

        Rectangle background = new Rectangle(500, 350);
        background.setFill(Color.WHITE);
        background.setStroke(Color.LIGHTGRAY);
        background.setStrokeWidth(1);
        background.setArcWidth(20);
        background.setArcHeight(20);

        return new StackPane(background, updateForm);
    }

    private void handleUpdatePassword() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "All fields are required!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "New passwords do not match!");
            return;
        }

        if (newPassword.length() < 6) {
            showAlert("Error", "New password must be at least 6 characters long!");
            return;
        }

        if (authController.updatePassword(oldPassword, newPassword)) {
            showAlert("Success", "Password updated successfully!");
            interfaceManager.showMainScreen();
        } else {
            showAlert("Error", "Failed to update password! Please check your current password.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}