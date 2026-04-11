package client;

import model.AuthData;
import model.GameData;
import ui.BoardDrawer;

public class GameplayUI implements MessageHandler {
    private WebSocketFacade command;
    private int gameID;
    private String authToken;
    private String teamColor;
    private GameData currentGame;
    public GameplayUI(WebSocketFacade command, int gameID, String authToken, String teamColor, GameData currentGame) {
        // message handler
        this.command= command;
        this.gameID = gameID;
        this.authToken = authToken;
        this.teamColor = teamColor;
        this.currentGame = currentGame;

    }
    @Override
    public void onMessage(String message) {
        // hanlde message from server

    }

    public boolean eval(String input) {
        var parts = input.split(" ");
        switch (parts[0]) {
            case "help" -> help();
            case "redraw" -> redraw();
            case "move" -> move();
            case "resign" -> resign();
            case "leave" -> leave();
            case "highlight" -> highlight();
            default -> helpInfo();
        }
        return false;
    }

    private void help() {
        System.out.println("redraw");
        System.out.println("move <LOCATION>");
        System.out.println("resign");
        System.out.println("leave");
        System.out.println("highlight <PIECE>");
    }

    private void redraw() {

    }


}
