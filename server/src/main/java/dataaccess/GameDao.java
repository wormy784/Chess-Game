package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class GameDao {
    private int nextGameID = 1;
    // hashmap to store games
    private final HashMap<Integer, GameData> games = new HashMap<>();

    public void createGame(GameData g) throws DataAccessException {
        GameData newGame = new GameData(nextGameID, g.whiteUsername(), g.blackUsername(),
                g.gameName(), g.game());

        // Add to hashmap
        games.put(newGame.gameID(), newGame);
        //increment gameid
        nextGameID++;
    }

    public GameData getGame(int gameId) throws DataAccessException {
        return games.get(gameId);
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    public void updateGame(GameData g) throws DataAccessException {
        games.put(g.gameID(), g);
    }

    public void clear() throws DataAccessException {
        // clear the hashmap
        games.clear();
    }
}