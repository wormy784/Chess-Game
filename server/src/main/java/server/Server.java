package server;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import io.javalin.*;
import server.handler.*;
import service.ClearService;
import service.UserService;

public class Server {
    UserDao userDao = new UserDao();
    AuthDao authDao = new AuthDao();
    GameDao gameDao = new GameDao();
    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        //clear
        ClearHandler clearHandler = new ClearHandler(new ClearService(userDao, authDao, gameDao));
        javalin.delete("/db", clearHandler::clear);
        //register
        RegisterHandler registerHandler = new RegisterHandler(new UserService(userDao, authDao));
        javalin.post("/user", registerHandler::register);
        // login
        LoginHandler loginHandler = new LoginHandler(new UserService(userDao, authDao));
        javalin.post("/session", loginHandler::login);
        // logout
        LogoutHandler logoutHandler = new LogoutHandler(new UserService(userDao, authDao));
        javalin.delete("/session", logoutHandler::logout);
        // create game
        CreateGameHandler createGameHandler = new CreateGameHandler();
        javalin.post("/game", createGameHandler::createGame);
        // join game
        JoinHandler joinHandler = new JoinHandler(new UserService(userDao, authDao));
        javalin.post("/game", joinHandler::join);
        // list games
        ListHandler listHandler = new ListHandler(new UserService(userDao, authDao));
        javalin.post("/game", listHandler::list);



    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
