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

import main.java.com.vss.view.InterfaceManager;
import main.java.com.vss.view.components.UIComponents;
import main.java.com.vss.controller.AuthController;

public class LoginScreen {
    private final InterfaceManager interfaceManager;
    private final AuthController authController;
    private final VBox root;

    private TextField usernameField;
    private PasswordField passwordField;

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

    public LoginScreen(InterfaceManager interfaceManager, AuthController authController) {
        this.interfaceManager = interfaceManager;
        this.authController = authController;

        root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        root.getChildren().addAll(
                createTitle(),
                createLoginBox()
        );
    }

    public VBox getRoot() {
        return root;
    }


    private Text createTitle() {
        asciiText.setFont(Font.font("Monospaced", 14));

        return asciiText;
    }

    private StackPane createLoginBox() {
        // Creăm câmpurile pentru username și parolă
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setFont(Font.font("DejaVu Sans Mono", 14));
        usernameField.setPrefWidth(300);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setFont(Font.font("DejaVu Sans Mono", 14));
        passwordField.setPrefWidth(300);

        // Creăm butoanele
        Button loginButton = UIComponents.createButton("Login", event -> handleLogin());
        Button createAccountButton = UIComponents.createButton("Create Account", event -> handleCreateAccount());

        // Aranjăm butoanele într-un container
        HBox buttonContainer = new HBox(20, loginButton, createAccountButton);
        buttonContainer.setAlignment(Pos.CENTER);

        // Creăm titlul formularului
        Label title = new Label("LOGIN");
        title.setFont(Font.font("DejaVu Sans Mono", 24));
        title.setTextFill(Color.web("#2c3e50"));

        // Creăm formularul
        VBox loginForm = new VBox(15);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setPadding(new Insets(20));
        loginForm.getChildren().addAll(
                title,
                UIComponents.createLabeledInput("Username:", usernameField),
                UIComponents.createLabeledInput("Password:", passwordField),
                buttonContainer
        );

        // Creăm fundalul formularului
        Rectangle background = new Rectangle(400, 300);
        background.setFill(Color.WHITE);
        background.setStroke(Color.LIGHTGRAY);
        background.setStrokeWidth(1);
        background.setArcWidth(20);
        background.setArcHeight(20);

        // Combinăm totul într-un StackPane
        return new StackPane(background, loginForm);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password are required!");
            return;
        }

        if (authController.authenticate(username, password)) {
//            interfaceManager.showMainScreen();
            System.out.println("Logged in successfully!");
        } else {
            showAlert("Error", "Invalid username or password!");
        }
    }

    private void handleCreateAccount() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password are required!");
            return;
        }

        if (authController.createAccount(username, password)) {
            showAlert("Success", "Account created successfully! You can now log in.");
        } else {
            showAlert("Error", "Username already exists!");
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
