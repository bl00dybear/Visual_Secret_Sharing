package main.java.com.vss.view;

import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.com.vss.controller.AuthController;
import main.java.com.vss.view.screens.LoginScreen;

public class InterfaceManager {
    private final Stage primaryStage;
    private final AuthController authController;

    public InterfaceManager(Stage primaryStage){
        this.primaryStage = primaryStage;
        this.authController = new AuthController();
    }

    public void showLoginScreen(){
        LoginScreen loginScreen = new LoginScreen(this,authController);
        Scene scene = new Scene(loginScreen.getRoot());
        primaryStage.setScene(scene);
    }
}
