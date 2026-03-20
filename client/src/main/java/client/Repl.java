package client;

import java.util.Scanner;

public class Repl {

    private final ServerFacade facade;

    public Repl(ServerFacade facade) {
        this.facade = facade;
    }
    Scanner scanner = new Scanner(System.in) ;

    private void run() {
        while (true) {
            // get user input
            System.out.print("Welcome to ChessGame (its the exact same as chess but without castling and en passant" +
                    " since i don't know how to implement them!). ");
            System.out.print("Type Help to display possible actions and get started");
            String input = scanner.nextLine();
        }
    }

}

