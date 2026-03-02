package service;
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

    }
}