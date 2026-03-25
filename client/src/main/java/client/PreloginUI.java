package client;

import model.AuthData;
import model.LoginRequest;
import model.RegisterRequest;


public class PreloginUI {

    private final ServerFacade facade;

    public PreloginUI(ServerFacade facade) {
        this.facade = facade;
    }

    public AuthData eval(String input) {
        var parts = input.split(" ");
        switch (parts[0]) {
            case "help" -> helpInfo();
            case "quit" -> quitInfo();
            case "login" -> {
                if (parts.length < 3) {
                    System.out.println("Please provide username and password");
                    return null;
                }
                return loginInfo(parts[1], parts[2]);
            }
            case "register" -> {
                if (parts.length < 4) {
                    System.out.println("Please provide username, password, and email");
                    return null;
                }
                return registerInfo(parts[1], parts[2], parts[3]);
            }
            default -> helpInfo();
        }
        return null;
    }

    private void helpInfo() {
        //display commands
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println("login <USERNAME> <PASSWORD>");
        System.out.println("quit");
        System.out.println("help");
    }

    private void quitInfo() {
        // close program
        System.exit(0);
    }

    private AuthData loginInfo(String username, String password) {
        //prompt user to input login information, calls server login api to log in the user.
        // when logged in, client should transition to post login UI
        LoginRequest request = new LoginRequest(username, password);
        try {
            AuthData auth = facade.login(request);
            System.out.println("Logged in successfully.");
            return auth;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private AuthData registerInfo(String username, String password, String email) {
        // prompt user to input registration information. calls the server register API to register and login the user.
        // if registered, client should be logged in and transition to postlogin UI
        RegisterRequest request = new RegisterRequest(username, password, email);
        try {
            AuthData auth = facade.register(request);
            System.out.println("Registered and logged in successfully.");
            return auth;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

}
