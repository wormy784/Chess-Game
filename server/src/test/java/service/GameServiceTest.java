package service;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

public class GameServiceTest {
    private GameDao gameDao;
    private AuthDao authDao;
    private GameService gameService;
    private UserService userService;

    @BeforeEach
    public void setup() {
        gameDao = new GameDao();
        authDao = new AuthDao();
        gameService = new GameService(gameDao, authDao);
        userService = new UserService(new UserDao(), authDao);
    }

    //pos creategame test
    @Test
    public void createGameSuccess() throws DataAccessException {
        // test register
        AuthData token = userService.register("testuser", "password123", "test@gmail.com");
        int gameID = gameService.createGame(token.authToken(), "MyGame");
        // return valid id
        Assertions.assertTrue(gameID > 0, "We should get an ID back");
        // is name right
        var game = gameDao.getGame(gameID);
        Assertions.assertNotNull(game, "Game should exist already");
        Assertions.assertEquals("MyGame", game.gameName());
    }
    // neg create game test
    @Test
    public void createGameFailure() {
        // create game with fake token
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameService.createGame("faketoken123", "fakeGame");
        }, "Should throw an exception for an invalid authToken");
    }

    //pos test listgames
    @Test
    public void listGamesSuccess() throws DataAccessException {
        //register user to get token
        AuthData token = userService.register("username", "password123", "something@gmail.com");
        // create game 1 and 2 with token
        gameService.createGame(token.authToken(), "Game 1");
        gameService.createGame(token.authToken(), "Game 2");
        //get collection of games
        var games = gameService.listGames(token.authToken());
        // assert collection is size of 2?
        Assertions.assertEquals(2, games.size());
    }
    //neg test listgames
    @Test
    public void listGamesFailure() {
        // assert that it catches the trash token and throws an exception
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameService.listGames("stupidgarbagetoken");
        });
    }
    //post test join games
    @Test
    public void joinGameSuccess() throws DataAccessException {
        // register user and create game
        AuthData auth = userService.register("Jeff", "password123", "something@mail.com");
        int gameID = gameService.createGame(auth.authToken(), "Duel of the Fates");
        // join as black
        gameService.joinGame(auth.authToken(), gameID, "BLACK");
        // assert if gameDao says its legit
        var game = gameDao.getGame(gameID);
        Assertions.assertEquals("Jeff", game.blackUsername(), "The black player should be Jeff");
    }
    // neg test join game
    @Test
    public void joinGameFailure() throws DataAccessException {
        AuthData alice = userService.register("Alice", "password123", "alice@mail.com");
        int gameID = gameService.createGame(alice.authToken(), "Alice's Game");
        gameService.joinGame(alice.authToken(), gameID, "WHITE");
        // register bob
        AuthData bob = userService.register("Bob", "password123", "bob@mail.com");

        // bob tries to take team white
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(bob.authToken(), gameID, "WHITE");
        }, "Should throw an exception because the WHITE team is already taken");
    }
}
