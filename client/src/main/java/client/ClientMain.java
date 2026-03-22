package client;

import chess.*;
public class ClientMain {
    public static void main(String[] args) {
        //facade
        ServerFacade facade = new ServerFacade(8080);
        // create repl with facade
        Repl repl = new Repl(facade);
        repl.run();
    }
}
