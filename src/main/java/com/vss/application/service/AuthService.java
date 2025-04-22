package main.java.com.vss.application.service;

public class AuthService {
    private static AuthService instance;

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }


    public boolean authenticate(String username, String password) {
        if ("admin".equals(username) && "admin".equals(password)) {

            return true;
        }
        return false;
    }

    public boolean createAccount(String username, String password) {
        return !"admin".equals(username);
    }
}
