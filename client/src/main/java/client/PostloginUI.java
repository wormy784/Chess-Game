package client;

import model.*;

import java.util.Scanner;

public class PostloginUI {

    private final ServerFacade facade;
    private AuthData authToken;

    public PostloginUI(ServerFacade facade, AuthData authToken) {
        this.facade = facade;
        this.authToken = authToken;
    }
    Scanner scanner = new Scanner(System.in) ;

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
                playGameInfo(Integer.parseInt(parts[1]), parts[2]);
            }
            case "observe" -> {
                if (parts.length < 2) {
                    System.out.println("Please provide a name for the game you want to observe.");
                    return false;
                }
                observeGameInfo(Integer.parseInt(parts[1]));
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
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listGamesInfo() {
        // list games
        try {
            var games = facade.listGames(authToken);
            for (var game : games.games()){
                System.out.println(game.gameName());
                }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void playGameInfo(int gameNumber, String teamColor) {
        // Allows the user to specify which game they want to join and what color they want to play.
        // They should be able to enter the number of the desired game. Your client will need to keep track of which
        // number corresponds to which game from the last time it listed the games. Calls the server join API to join
        // the user to the game.
        JoinRequest request = new JoinRequest(teamColor, gameNumber);
        try {
            facade.joinGame(request, authToken);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private void observeGameInfo(int gameNumber) {
        JoinRequest request = new JoinRequest(null, gameNumber);
        try {
            facade.joinGame(request, authToken);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

}
