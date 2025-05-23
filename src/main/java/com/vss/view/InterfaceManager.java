package main.java.com.vss.view;

import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.com.vss.controller.AuthController;
import main.java.com.vss.controller.DecryptController;
import main.java.com.vss.controller.EncryptController;
import main.java.com.vss.view.screens.DecryptScreen;
import main.java.com.vss.view.screens.EncryptScreen;
import main.java.com.vss.view.screens.LoginScreen;
import main.java.com.vss.view.screens.MainScreen;

public class InterfaceManager {
    private final Stage primaryStage;
    private final AuthController authController;
    private final EncryptController encryptController;
    private final DecryptController decryptController;

    public InterfaceManager(Stage primaryStage){
        this.primaryStage = primaryStage;
        this.authController = new AuthController();
        this.encryptController = new EncryptController(this);
        this.decryptController = new DecryptController(this);
    }

    public void showLoginScreen(){
        LoginScreen loginScreen = new LoginScreen(this,authController);
        Scene scene = new Scene(loginScreen.getRoot());
        primaryStage.setScene(scene);
    }

    public void showMainScreen() {
        MainScreen mainScreen = new MainScreen(this, authController);
        Scene scene = new Scene(mainScreen.getRoot());
        primaryStage.setScene(scene);
    }

    public void showEncryptScreen() {
        EncryptScreen encryptScreen = new EncryptScreen(this, authController, encryptController);
        Scene scene = new Scene(encryptScreen.getRoot());
        primaryStage.setScene(scene);
    }

    public void showDecryptScreen() {
        DecryptScreen decryptScreen = new DecryptScreen(this,authController,decryptController);
        Scene scene = new Scene(decryptScreen.getRoot());
        primaryStage.setScene(scene);
    }
}
