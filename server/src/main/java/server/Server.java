package server;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import io.javalin.*;
import server.handler.*;
import service.ClearService;
import service.GameService;
import service.UserService;


public class Server {
    UserDao userDao = new UserDao();
    AuthDao authDao = new AuthDao();
    GameDao gameDao = new GameDao();
    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");

            // --- ADD THIS MAPPER CONFIGURATION ---
            config.jsonMapper(new io.javalin.json.JsonMapper() {
                private final com.google.gson.Gson gson = new com.google.gson.Gson();

                @Override
                public String toJsonString(@org.jetbrains.annotations.NotNull Object obj, @org.jetbrains.annotations.NotNull java.lang.reflect.Type type) {
                    return gson.toJson(obj);
                }

                @Override
                public <T> T fromJsonString(@org.jetbrains.annotations.NotNull String json, @org.jetbrains.annotations.NotNull java.lang.reflect.Type targetType) {
                    return gson.fromJson(json, targetType);
                }
            });
            // --------------------------------------
        });

        // Your existing Services and Handlers stay exactly the same:
        GameService gameService = new GameService(gameDao, authDao);
        UserService userService = new UserService(userDao, authDao);

        // clear
        ClearHandler clearHandler = new ClearHandler(new ClearService(userDao, authDao, gameDao));
        javalin.delete("/db", clearHandler::clear);

        // register
        RegisterHandler registerHandler = new RegisterHandler(new UserService(userDao, authDao));
        javalin.post("/user", registerHandler::register);

        // login
        LoginHandler loginHandler = new LoginHandler(new UserService(userDao, authDao));
        javalin.post("/session", loginHandler::login);

        // logout
        LogoutHandler logoutHandler = new LogoutHandler(new UserService(userDao, authDao));
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
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
