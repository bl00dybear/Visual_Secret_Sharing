package main.java.com.vss.view.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
//import main.java.com.vss.MainController;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.java.com.vss.controller.AuthController;
import main.java.com.vss.view.InterfaceManager;
import main.java.com.vss.view.components.UIComponents;

public class MainScreen {
    private final InterfaceManager interfaceManager;
    private final AuthController authController;

    private VBox root;

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

    public MainScreen(InterfaceManager interfaceManager, AuthController authController){
        this.interfaceManager = interfaceManager;
        this.authController = authController;

        root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        root.getChildren().addAll(
                createTitle(),
                createButtonPanel()
        );

    }

    private Text createTitle() {
        asciiText.setFont(Font.font("Monospaced", 14));

        return asciiText;
    }

    private VBox createButtonPanel() {
        Button encryptButton = UIComponents.createButton("Encrypt", event -> handleEncrypt());
        Button decryptButton = UIComponents.createButton("Decrypt", event -> handleDecrypt());
        Button updatePasswordButton = UIComponents.createButton("Update Password", event -> interfaceManager.showUpdatePasswordScreen());
        Button logoutButton = UIComponents.createButton("Logout", event -> interfaceManager.showLoginScreen());
        Button deleteAccButton = UIComponents.createButton("Delete Account", event -> authController.deleteAccount());
        Button exitButton = UIComponents.createButton("Exit", event -> System.exit(0));

        VBox buttonContainer = new VBox(10, encryptButton, decryptButton, updatePasswordButton,logoutButton, deleteAccButton, exitButton);
        buttonContainer.setAlignment(Pos.CENTER);

        buttonContainer.setPadding(new Insets(20));

        return buttonContainer;
    }

    public VBox getRoot() {
        return root;
    }

    private void handleEncrypt() {
        System.out.println("Encrypting...");
        interfaceManager.showEncryptScreen();
    }
    private void handleDecrypt() {
        System.out.println("Decrypting...");
        interfaceManager.showDecryptScreen();
    }

}
