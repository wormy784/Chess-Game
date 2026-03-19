package client;

import model.AuthData;
import model.RegisterRequest;
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
        RegisterRequest request = new RegisterRequest("username", "password123", "email@gmail.com");
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
}
