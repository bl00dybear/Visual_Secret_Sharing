package main.java.com.vss.controller;

import main.java.com.vss.application.service.AuthService;

public class AuthController {
    private final AuthService authService;

    public AuthController() {
        this.authService = AuthService.getInstance();
    }

    public boolean authenticate(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return false;
        }

        // Logică de autentificare simplificată pentru dezvoltare
        return authService.authenticate(username, password);
    }

    public boolean createAccount(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return false;
        }

        // Logică de creare cont simulată - va fi implementată complet în sprint-ul următor
        return authService.createAccount(username, password);
    }
}
