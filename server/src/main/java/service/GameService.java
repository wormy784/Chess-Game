package service;
import chess.ChessGame;
import dataaccess.*;
import model.*;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
public class GameService {
    // add fields for game and authdao
    private GameDao gameDao;
    private AuthDao authDao;

    public GameService(GameDao gameDao, AuthDao authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }
    public int createGame(String authToken, String gameName) throws DataAccessException {
        // verify authtoken
        var auth = authDao.getAuth(authToken);
        // throw exception if null
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        // create gameData
        GameData newGame = new GameData(0, null, null, gameName, new ChessGame());
        return gameDao.createGame(newGame);
    }
    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        // verify authtoken
        var auth = authDao.getAuth(authToken);
        // throw exception if null
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        // get all games and return
        return gameDao.listGames();
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException {
        // verify authtoken
        var auth = authDao.getAuth(authToken);
        // throw exception if null
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        // get game by id
        var game = gameDao.getGame(gameID);
        // if not exist throw bad request
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }
        //make sure color is real color
        if (!Objects.equals(playerColor, "WHITE") && (!Objects.equals(playerColor, "BLACK"))) {
            throw new DataAccessException("Error; not a valid team color");
        }
        // check if color is already taken
        if (Objects.equals(playerColor, "WHITE") && game.whiteUsername() != null){
            // if taken throw already taken exception
            throw new DataAccessException("Error: already taken");
        }
        if (Objects.equals(playerColor, "BLACK") && game.blackUsername() != null){
            // if taken throw already taken exception
            throw new DataAccessException("Error: already taken");
        }

        // update game with player's username
        GameData updatedGame = null;
        if (Objects.equals(playerColor, "WHITE")) {
            updatedGame = new GameData(gameID, auth.username(), game.blackUsername(), game.gameName(), game.game());
        }
        if (Objects.equals(playerColor, "BLACK")) {
            updatedGame = new GameData(gameID, game.whiteUsername(), auth.username(), game.gameName(), game.game());
        }
        //save the updated game
        gameDao.updateGame(updatedGame);
    }
}