package service;
import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.*;
import model.*;
import java.util.Collection;
import java.util.Objects;

public class GameService {
    // add fields for game and authdao
    private IGameDao gameDao;
    private IAuthDao authDao;

    public GameService(IGameDao gameDao, IAuthDao authDao) {
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
        if (playerColor != null && !Objects.equals(playerColor, "WHITE") && (!Objects.equals(playerColor, "BLACK"))) {
            throw new DataAccessException("Error: bad request");
        }
        // allow observers
        if (playerColor == null) {
            return;
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

    public GameData getGame(String authToken, int gameID) throws DataAccessException {
        // verify authtoken
        var auth = authDao.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return gameDao.getGame(gameID);

    }

    public GameData makeMove(String authToken, int gameID, ChessMove move) throws DataAccessException, InvalidMoveException {
        // verify auth token
        var auth = authDao.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        // get game by gameid
        var game = getGame(authToken, gameID);
        // apply move (if bad move throw invalid movoe exception
        try {
            game.game().makeMove(move);
        } catch (InvalidMoveException e) {
            throw new InvalidMoveException(e.getMessage());
        }
        // save updated game back to database bro
        gameDao.updateGame(game);
        return game;
    }

    public void  leaveGame(String authToken, int gameID) throws DataAccessException {
        // get auth token
        var auth = authDao.getAuth(authToken);
        // check if its null
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        var game = gameDao.getGame(gameID);



        if (game == null) {
            return;
        }
        // remove player from slot of color
        GameData updatedGame;
        if (auth.username().equals(game.whiteUsername())) {
            updatedGame = new GameData(gameID, null, game.blackUsername(), game.gameName(), game.game());
        } else if (auth.username().equals(game.blackUsername())) {
            updatedGame = new GameData(gameID, game.whiteUsername(), null, game.gameName(), game.game());
        } else {
            // observer i think
            return;
        }
        gameDao.updateGame(updatedGame);
    }

    public GameData resignGame(String authToken, int gameID) throws DataAccessException{
        // verify auth token
        var auth = authDao.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        // get game by gameid
        var game = getGame(authToken, gameID);
        game.game().setGameOver(true);
        // save updated game back to database bro
        gameDao.updateGame(game);
        return game;
    }
}