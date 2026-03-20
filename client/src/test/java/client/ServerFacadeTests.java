package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;




public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerPosTest() throws Exception {
        //request
        RegisterRequest request = new RegisterRequest("username", "password123", "email@gmail.com");
        // register that request
        AuthData testResult = facade.register(request);
        Assertions.assertNotNull(testResult);
    }

    @Test
    public void registerNegTest() throws Exception {
        RegisterRequest request = new RegisterRequest("username", "password123", "email@gmail.com");
        facade.register(request);
        new RegisterRequest("username", "password123", "email@gmail.com");
        Assertions.assertThrows(Exception.class, () -> facade.register(request));
    }

    @Test
    public void loginPosTest() throws Exception {
        //register request
        RegisterRequest request = new RegisterRequest("username", "password123", "email@gmail.com");
        // register request
        facade.register(request);
        // login request
        LoginRequest request2 = new LoginRequest("username", "password123");
        // login with username
        AuthData loginTest = facade.login(request2);
        Assertions.assertNotNull(loginTest);
    }

    @Test
    public void loginNegTest() throws Exception {
        //register request
        RegisterRequest request = new RegisterRequest("username", "password123", "email@gmail.com");
        // register request
        facade.register(request);
        // login request with fake username
        LoginRequest request2 = new LoginRequest("usernamefake", "password123");
        Assertions.assertThrows(Exception.class, () -> facade.login(request2));
    }

    @Test
    public void logoutPosTest() throws Exception {
        //register request
        RegisterRequest request = new RegisterRequest("username", "password123", "email@gmail.com");
        // register request
        facade.register(request);
        // login request
        LoginRequest request2 = new LoginRequest("username", "password123");
        // login with username
        AuthData authToken = facade.login(request2);
        // try logging out, make sure nothing throws bro
        Assertions.assertDoesNotThrow(() -> facade.logout(authToken));
    }

    @Test
    public void logoutNegTest() throws Exception {
        // fake token
        AuthData fakeToken = new AuthData("token", "jimmyjohn");
        // try logging out, make sure nothing throws bro
        Assertions.assertThrows(Exception.class, () -> facade.logout(fakeToken));
    }

    @Test
    public void createGamePosTest() throws Exception {
        //register request
        RegisterRequest request = new RegisterRequest("username", "password123", "email@gmail.com");
        // register request
        facade.register(request);
        // login request
        LoginRequest request2 = new LoginRequest("username", "password123");
        // login with username
        AuthData authToken = facade.login(request2);
        CreateGameRequest gameName = new CreateGameRequest("FortniteBattlePass");
        Assertions.assertNotNull(facade.createGame(gameName, authToken));
    }

    @Test
    public void createGameNegTest() throws Exception {
        // fake token
        AuthData fakeToken = new AuthData("token", "jimmyjohn");
        // try creating the game, make sure it throws bro since its a fake token
        CreateGameRequest gameName = new CreateGameRequest("FortniteBattlePass");
        Assertions.assertThrows(Exception.class, () -> facade.createGame(gameName, fakeToken));
    }

    @Test
    public void listGamesPosTest() throws Exception {
        //register request
        RegisterRequest request = new RegisterRequest("username", "password123", "email@gmail.com");
        // register request
        facade.register(request);
        // login request
        LoginRequest request2 = new LoginRequest("username", "password123");
        // login with username
        AuthData authToken = facade.login(request2);
        CreateGameRequest gameName = new CreateGameRequest("FortniteBattlePass");
        facade.createGame(gameName, authToken);
        CreateGameRequest gameName2 = new CreateGameRequest("Minecraft");
        facade.createGame(gameName2, authToken);
        ListGamesResult games = facade.listGames(authToken);
        Assertions.assertEquals(2, games.games().size());
    }

    @Test
    public void listGamesNegTest() throws Exception {
        // fake token and assert that it actually trhows bro
        AuthData fakeToken = new AuthData("token", "jimmyjohn");
        Assertions.assertThrows(Exception.class, () -> facade.listGames(fakeToken));
    }

    @Test
    public void joinGamePosTest() throws Exception {
        //register request
        RegisterRequest request = new RegisterRequest("username", "password123", "email@gmail.com");
        // register request
        facade.register(request);
        // login request
        LoginRequest request2 = new LoginRequest("username", "password123");
        // login with username
        AuthData authToken = facade.login(request2);
        CreateGameRequest gameName = new CreateGameRequest("FortniteBattlePass");
        CreateGameResult gameID = facade.createGame(gameName, authToken);
//      //System.out.println(gameID.gameID());
        JoinRequest joinRequest = new JoinRequest("WHITE", gameID.gameID());
        // assert it doesnt throw
//        System.out.println(joinRequest);
//        System.out.println(authToken);
        Assertions.assertDoesNotThrow(() -> facade.joinGame(joinRequest, authToken));
    }

    @Test
    public void joinGameNegTest() throws Exception {
        // try to join a game that doesn't exist with fake id
        AuthData fakeToken = new AuthData("token", "jimmyjohn");
        JoinRequest joinRequest = new JoinRequest("WHITE", 66);
        // assert it doesnt throw
        Assertions.assertThrows(Exception.class, () -> facade.joinGame(joinRequest, fakeToken));
    }
}
