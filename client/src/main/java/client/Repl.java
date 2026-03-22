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
        System.out.print("Welcome to ChessGame (its the exact same as chess but without castling and en passant" +
                " since i don't know how to implement them!). ");
        System.out.print("Type Help to display possible actions and get started");
        PreloginUI preStuff = new PreloginUI(facade);

        while (true) {
            //check if person is logged in or not
            if (authToken != null) {
                loggedIn = true;
            }
            // get user input
            String input = scanner.nextLine();
            if (loggedIn) {
                PostloginUI stuff = new PostloginUI(facade, authToken);
                // pass input to postloginUI
                var result = stuff.eval(input);
                if (result) {
                    loggedIn = false;
                    authToken = null;
                }
            }
            else {

                // pass input into preloginUI
                var result = preStuff.eval(input);
                if (result != null) {
                    authToken = result;
                    loggedIn = true;
                }
            }
        }
    }

}

