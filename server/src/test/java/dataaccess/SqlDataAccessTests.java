package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import service.ClearService;

public class SqlDataAccessTests {
    private SqlUserDao userDao;
    private SqlAuthDao authDao;
    private SqlGameDao gameDao;

    @BeforeEach
    public void setup() throws DataAccessException {
        // clear all the stuff before each test we run
        userDao = new SqlUserDao();
        userDao.clear();
        authDao = new SqlAuthDao();
        authDao.clear();
        gameDao = new SqlGameDao();
        gameDao.clear();
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        // create userdata object
        UserData user = new UserData("testuser", "password123", "test@email.com");
        userDao.createUser(user);
        userDao.clear();
        Assertions.assertNull(userDao.getUser("testuser"));
    }

    //pos creategame test
    @Test
    public void createGameSuccess() throws DataAccessException {
        // create gamedata object
        GameData game = new GameData(0, null, null, "MyGame", new ChessGame());
        int gameID = gameDao.createGame(game);
        // assert id greater than 0
        Assertions.assertTrue(gameID > 0, "We should get an ID back");
        game = gameDao.getGame(gameID);
        Assertions.assertNotNull(game, "Game should exist already");
        Assertions.assertEquals("MyGame", game.gameName());
    }
    // neg create game test
    @Test
    public void createGameFailure() {
        // create game with fake token
        GameData game = new GameData(0, null, null, null, new ChessGame());
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDao.createGame(game);
        }, "Should throw an exception for an invalid authToken");
    }

    //register pos test
    @Test
    public void registerSuccess() throws DataAccessException {
        // test register
        UserData user = new UserData("randy", "randy123", "randy@gmail.com");
        userDao.createUser(user);
        Assertions.assertNotNull(userDao.getUser(user.username()));
        Assertions.assertEquals("randy", userDao.getUser("randy").username());
    }
    //register neg test
    @Test
    public void registerUsernameTaken() throws DataAccessException {
        // test register
        UserData user = new UserData("randy", "randy123", "randy@gmail.com");
        userDao.createUser(user);
        Assertions.assertNotNull(userDao.getUser(user.username()));
        Assertions.assertEquals("randy", userDao.getUser("randy").username());
    }
}
