package client;

import model.AuthData;

import java.util.Scanner;

public class Repl {

    private final ServerFacade facade;
    private boolean loggedIn = false;

    private AuthData authToken;

    public Repl(ServerFacade facade) {
        this.facade = facade;
    }
    Scanner scanner = new Scanner(System.in) ;

    public void run() {
        System.out.print("Welcome to ChessGame!\n");
        System.out.print("Type help to display possible actions and get started\n");
        PreloginUI preStuff = new PreloginUI(facade);
        PostloginUI stuff = null;

        while (true) {
            try {
                //check if person is logged in or not
                if (authToken != null) {
                    loggedIn = true;
                }
                // get user input
                String input = scanner.nextLine();
                if (loggedIn) {
                    // pass input to postloginUI
                    if (stuff != null) {
                        var result = stuff.eval(input);
                        if (result) {
                            loggedIn = false;
                            authToken = null;
                        }
                    }
                }
                else {
                    // pass input into preloginUI
                    var result = preStuff.eval(input);
                    if (result != null) {
                        authToken = result;
                        loggedIn = true;
                        stuff = new PostloginUI(facade, authToken);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

        }
    }

}

