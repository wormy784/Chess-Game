package server;

import dataaccess.*;
import io.javalin.*;
import server.handler.*;
import service.ClearService;
import service.GameService;
import service.UserService;


public class Server {
    SqlUserDao userDao = new SqlUserDao();
    SqlAuthDao authDao = new SqlAuthDao();
    SqlGameDao gameDao = new SqlGameDao();
    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        GameService gameService = new GameService(gameDao, authDao);
        UserService userService = new UserService(userDao, authDao);
        ClearService clearService = new ClearService(userDao, authDao, gameDao);
        // Register your endpoints and exception handlers here.

        //clear
        ClearHandler clearHandler = new ClearHandler(clearService);
        javalin.delete("/db", clearHandler::clear);
        //register
        RegisterHandler registerHandler = new RegisterHandler(userService);
        javalin.post("/user", registerHandler::register);
        // login
        LoginHandler loginHandler = new LoginHandler(userService);
        javalin.post("/session", loginHandler::login);
        // logout
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        javalin.delete("/session", logoutHandler::logout);
        // create game
        CreateGameHandler createGameHandler = new CreateGameHandler(gameService);
        javalin.post("/game", createGameHandler::createGame);
        // join game
        JoinHandler joinHandler = new JoinHandler(gameService);
        javalin.put("/game", joinHandler::join);
        // list games
        ListHandler listHandler = new ListHandler(gameService);
        javalin.get("/game", listHandler::list);



    }

    public int run(int desiredPort) {
        try {
            //create database
            new SqlSetup();
            javalin.start(desiredPort);
        } catch (DataAccessException e) {
            System.out.printf("Failed to initialize database: %s%n", e.getMessage());
        }
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
