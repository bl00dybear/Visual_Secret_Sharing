package main.java.com.vss.view;

import javafx.application.Application;
import javafx.stage.Stage;


public class VSSApplication extends Application {
    @Override
    public void start(Stage primaryStage){
        InterfaceManager interfaceManager = new InterfaceManager(primaryStage);

        // Afișăm ecranul de login inițial
        interfaceManager.showLoginScreen();

        // Configurăm fereastra principală
        primaryStage.setTitle("Visual Secret Sharing");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(800);
        primaryStage.show();
    }
}
