package client;

import chess.ChessBoard;
import chess.ChessGame;
import model.*;
import ui.BoardDrawer;
public class PostloginUI {

    private final ServerFacade facade;
    private AuthData authToken;

    private java.util.List<GameData> gamesList = new java.util.ArrayList<>();

    public PostloginUI(ServerFacade facade, AuthData authToken) {
        this.facade = facade;
        this.authToken = authToken;
    }
    public boolean eval(String input) {
        var parts = input.split(" ");
        switch (parts[0]) {
            case "help" -> helpInfo();
            case "logout" -> {
                if (logoutInfo()) {
                    return true;
                }
            }

            case "quit" -> quitInfo();
            case "create" -> {
                if (parts.length < 2) {
                    System.out.println("Please provide a name fo yo game.");
                    return false;
                }
                createGameInfo(parts[1]);
            }
            case "list" -> listGamesInfo();
            case "join" -> {
                if (parts.length < 3) {
                    System.out.println("Please provide a name for the game you want to join and a team color.");
                    return false;
                }
                try {
                    playGameInfo(Integer.parseInt(parts[1]), parts[2]);
                } catch (Exception e) {
                    System.out.println("Please provide a valid game number.");
                }
            }
            case "observe" -> {
                if (parts.length < 2) {
                    System.out.println("Please provide a name for the game you want to observe.");
                    return false;
                }
                try {
                    observeGameInfo(Integer.parseInt(parts[1]));
                } catch (Exception e) {
                    System.out.println("Please provide game number.");
                }
            }
            default -> helpInfo();
        }
        return false;
    }

    private void quitInfo() {
        // close program
        System.exit(0);
    }

    private void helpInfo() {
        //display commands
        System.out.println("create <NAME>");
        System.out.println("list");
        System.out.println("join <ID> [WHITE|BLACK]");
        System.out.println("observe <ID>");
        System.out.println("logout");
        System.out.println("quit");
        System.out.println("help");
    }

    private boolean logoutInfo() {
        // logout
        try {
            facade.logout(authToken);
            System.out.println("Logged out successfully.");
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void createGameInfo(String gameName) {
        //Allows the user to input a name for the new game. Calls the server create API to create the game.
        // This does not join the player to the created game; it only creates the new game in the server.
        CreateGameRequest request = new CreateGameRequest(gameName);
        try {
            facade.createGame(request, authToken);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void listGamesInfo() {
        // list games
        try {
            int count = 1;
            var games = facade.listGames(authToken);
            gamesList = new java.util.ArrayList<>(games.games());
            for (var game : games.games()){
                System.out.println(count + ". " + game.gameName() + " Players: " +  game.whiteUsername() + " " + game.blackUsername());
                count++;
                }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void playGameInfo(int gameNumber, String teamColor) {
        // Allows the user to specify which game they want to join and what color they want to play.
        // They should be able to enter the number of the desired game. Your client will need to keep track of which
        // number corresponds to which game from the last time it listed the games. Calls the server join API to join
        // the user to the game.
        JoinRequest request = new JoinRequest(teamColor, gamesList.get(gameNumber - 1).gameID());
        try {
            facade.joinGame(request, authToken);
            BoardDrawer drawn = new BoardDrawer();
            var chessGame = new ChessGame();
            drawn.drawBoard(chessGame.getBoard(), teamColor.equals("WHITE"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void observeGameInfo(int gameNumber) {
        if (gamesList.isEmpty()) {
            System.out.println("Please make sure games are available to observe.");
        }
        if (gameNumber < 1 || gameNumber > gamesList.size()) {
            System.out.println("Invalid game number.");
            return;
        }
        BoardDrawer drawn = new BoardDrawer();
        var chessGame = new ChessGame();
        drawn.drawBoard(chessGame.getBoard(), true);

    }

}
