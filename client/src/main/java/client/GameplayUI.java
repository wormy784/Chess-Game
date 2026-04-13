package client;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import ui.BoardDrawer;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

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
        var gson = new Gson();
        var serverMessage = gson.fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                var loadMessage = gson.fromJson(message, LoadGameMessage.class);
                currentGame = gson.fromJson(gson.toJson(loadMessage.game), GameData.class);
                redraw();
            }
            case NOTIFICATION -> {
                var notifMessage = gson.fromJson(message, NotificationMessage.class);
                System.out.println(notifMessage.message);
            }
            case ERROR -> {
                var errorMessage = gson.fromJson(message, ErrorMessage.class);
                System.out.println(errorMessage.errorMessage);
            }
        }

    }

    public boolean eval(String input) {
        var parts = input.split(" ");
        switch (parts[0]) {
            case "help" -> help();
            case "redraw" -> redraw();
            case "move" -> move((parts[1]), parts[2]);

            case "resign" -> resign();
            case "leave" -> {
                leave();
            return true;
            }
            case "highlight" -> highlight(parts[1]);
            default -> help();
        }
        return false;
    }

    private void help() {
        System.out.println("redraw");
        System.out.println("move <LOCATION> <DESTINATION>");
        System.out.println("resign");
        System.out.println("leave");
        System.out.println("highlight <PIECE>");
    }

    private void redraw() {
        if (currentGame == null) {
            System.out.println("No game loaded.");
            return;

        }
        BoardDrawer drawn = new  BoardDrawer();
        drawn.drawBoard(currentGame.game().getBoard(), "WHITE".equals(teamColor));
    }

    private void leave() {
        try {
            command.sendMessage(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void move(String location, String destination) {
        try {
            var newLocation = new ChessPosition(Character.getNumericValue(location.charAt(1)),location.charAt(0) - 'a' + 1);
            var newDestination= new ChessPosition(Character.getNumericValue(destination.charAt(1)),destination.charAt(0) - 'a' + 1);
            var move = new ChessMove(newLocation, newDestination, null);
            var moveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            command.sendMessage(moveCommand);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void resign() {
        try {
            command.sendMessage(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void highlight(String location) {
        try {
            var newLocation = new ChessPosition(Character.getNumericValue(location.charAt(1)),location.charAt(0) - 'a' + 1);
            var validMoves = currentGame.game().validMoves(newLocation);
            for (var move : validMoves) {
                System.out.println(move.getEndPosition());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void setWs(WebSocketFacade ws) {
        this.command = ws;
    }

}
