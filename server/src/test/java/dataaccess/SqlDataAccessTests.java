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
        GameData game = new GameData(0, null, null, null, new ChessGame());
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDao.createGame(game);
        }, "Should throw an exception for not making a game name");
    }

    //pos test listgames
    @Test
    public void listGamesSuccess() throws DataAccessException {
        //create two gamedata objects
        GameData game = new GameData(0, null, null, "Game1", new ChessGame());
        GameData game2 = new GameData(0, null, null, "Game2", new ChessGame());
        // add them
        gameDao.createGame(game);
        gameDao.createGame(game2);
        // call list games and get size
        var size = gameDao.listGames().size();
        // assert collection is size of 2?
        Assertions.assertEquals(2, size);
    }

    //neg test listgames
    @Test
    public void listGamesFailure() throws DataAccessException {
        // create a game then clear it t hen assert that the list is empty afterwards bro
        GameData game = new GameData(0, null, null, "Game1", new ChessGame());
        gameDao.createGame(game);
        gameDao.clear();
        Assertions.assertEquals(0, gameDao.listGames().size());
    }

    //post test join games
    @Test
    public void updateGameSuccess() throws DataAccessException {
        // create gamedata object, create new gaemdata with same id and username, then call updategame
        GameData game = new GameData(0, null, null, "Game1", new ChessGame());
        var gameID = gameDao.createGame(game);
        GameData updatedGame = new GameData(gameID, "testuser", null, "Game1", new ChessGame());
        gameDao.updateGame(updatedGame);
        Assertions.assertEquals("testuser", gameDao.getGame(gameID).whiteUsername());
    }

    // neg test join game
    @Test
    public void updateGameFailure() throws DataAccessException {
        // create gamedata object, create new gaemdata and call updategame with id that doesnt exist
        GameData game = new GameData(0, null, null, "Game1", new ChessGame());
        gameDao.updateGame(game);
        Assertions.assertNull(gameDao.getGame(66));
    }

    //register pos test
    @Test
    public void createUserSuccess() throws DataAccessException {
        // test register
        UserData user = new UserData("randy", "randy123", "randy@gmail.com");
        userDao.createUser(user);
        Assertions.assertNotNull(userDao.getUser(user.username()));
        Assertions.assertEquals("randy", userDao.getUser("randy").username());
    }

    @Test
    public void createUserFailure() throws DataAccessException {
        // test create user by making two user and making sure it asserts if the username is taken
        UserData user = new UserData("randy", "randy123", "randy@gmail.com");
        userDao.createUser(user);
        UserData user2 = new UserData("randy", "randy123", "randy@gmail.com");
        Assertions.assertThrows(DataAccessException.class, () -> {
            userDao.createUser(user2);
        });
    }

    @Test
    public void verifyUserSuccess() throws DataAccessException {
        // make a new user and verify it is created
        UserData user = new UserData("randy", "randy123", "randy@gmail.com");
        userDao.createUser(user);
        Assertions.assertTrue(userDao.verifyUser("randy", "randy123"));
    }

    @Test
    public void verifyUserFailure() throws DataAccessException {
        // verify a user with the wrong password and make it assert false
        UserData user = new UserData("randy", "randy123", "randy@gmail.com");
        userDao.createUser(user);
        Assertions.assertFalse(userDao.verifyUser("randy", "randy332"));
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        UserData user = new UserData("randy", "randy123", "randy@gmail.com");
        userDao.createUser(user);
        Assertions.assertNotNull(userDao.getUser("randy"));
    }

    @Test
    public void getUserFailure() throws DataAccessException {
        Assertions.assertNull(userDao.getUser("nobody"));
    }

    @Test
    public void createAuthSuccess() throws DataAccessException {
        AuthData token = new AuthData("token", "randy2itstrue");
        authDao.createAuth(token);
        Assertions.assertNotNull(authDao.getAuth(token.authToken()));
    }

    @Test
    public void createAuthFailure() throws DataAccessException {
        AuthData token = new AuthData(null, "randy2itstrue");
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDao.createAuth(token);
        });
    }

    @Test
    public void getAuthSuccess() throws DataAccessException {
        AuthData token = new AuthData("token", "randy2itstrue");
        authDao.createAuth(token);
        Assertions.assertNotNull(authDao.getAuth(token.authToken()));
        Assertions.assertEquals("randy2itstrue", authDao.getAuth(token.authToken()).username());
    }

    @Test
    public void getAuthFailure() throws DataAccessException {
        Assertions.assertNull(authDao.getAuth("tokennotreal"));
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        AuthData token = new AuthData("token", "randy2itstrue");
        authDao.createAuth(token);
        authDao.deleteAuth(token.authToken());
        Assertions.assertNull(authDao.getAuth(token.authToken()));
    }

    @Test
    public void deleteAuthFailure() throws DataAccessException {
        authDao.deleteAuth("token");
        Assertions.assertNull(authDao.getAuth("token"));
    }
}

