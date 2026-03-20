package client;

import model.AuthData;
import model.LoginRequest;
import model.RegisterRequest;

import java.util.Scanner;

public class PreloginUI {

    private final ServerFacade facade;
    private AuthData authToken;

    public PreloginUI(ServerFacade facade) {
        this.facade = facade;
    }
    Scanner scanner = new Scanner(System.in) ;

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
        //prompt user to input login information, calls server login api to login the user.
        // when logged in, client should transition to postlogin UI
        // System.out.println("Please enter a username and password: ");
        LoginRequest request = new LoginRequest(username, password);
        try {
            return facade.login(request);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    private AuthData registerInfo(String username, String password, String email) {
        // prompt user to input registration information. calls the server register API to register and login the user.
        // if registered, client should be logged in and transition to postlogin UI
        //System.out.println("Please enter a username, password, and email: ");
        RegisterRequest request = new RegisterRequest(username, password, email);
        try {
            return facade.register(request);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }

    }

}
