package client;

import model.AuthData;

import java.util.Scanner;

public class Repl {

    private final ServerFacade facade;

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
                // get user input
                String input = scanner.nextLine();
                //check if person is logged in or not
                if (authToken != null) {
                    stuff = handlePostLogin(stuff, input);
                } else {
                    stuff = handlePreLogin(preStuff, input);
                }


            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

        }
    }
    private PostloginUI handlePostLogin(PostloginUI stuff, String input) {
            // pass input to postloginUI
            if (stuff != null) {
                var result = stuff.eval(input);
                if (result) {
                    authToken = null;
                    return null;
            }
        }
        return stuff;
    }

    private PostloginUI handlePreLogin(PreloginUI preStuff, String input) {
        // pass input into preloginUI
        var result = preStuff.eval(input);
        if (result != null) {
            authToken = result;
            return new PostloginUI(facade, authToken);
        }
        return null;
    }
}

