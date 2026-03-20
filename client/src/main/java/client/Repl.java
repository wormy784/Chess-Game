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

    private void run() {
        System.out.print("Welcome to ChessGame (its the exact same as chess but without castling and en passant" +
                " since i don't know how to implement them!). ");
        System.out.print("Type Help to display possible actions and get started");
        while (true) {
            //check if person is logged in or not
            if (authToken != null) {
                loggedIn = true;
            }
            // get user input
            String input = scanner.nextLine();
            if (loggedIn) {
                // pass input to postloginUI

            }
            else {
                // pass input into preloginUI
            }
        }
    }

}

