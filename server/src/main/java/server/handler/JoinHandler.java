package server.handler;

import service.GameService;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import java.util.Map;

public class JoinHandler {
    private final GameService gameService;

    public JoinHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void join(Context ctx) {
        // record gets color and id
        record JoinRequest(String playerColor, int gameID) {}
        // get the auth token from the header
        String authToken = ctx.header("authorization");

        // Parse the body into record bro
        var body = ctx.bodyAsClass(JoinRequest.class);

        // check auth token
        //401
        if (authToken == null) {
            ctx.status(401);
            ctx.json(Map.of("message", "Error: unauthorized"));
            return;
        }
        //if color null or not one of the options then error
        //401
        if (body.playerColor() == null || (!body.playerColor().equals("WHITE") && !body.playerColor().equals("BLACK"))) {
            ctx.status(400);
            ctx.json(Map.of("message", "Error: bad request"));
            return;
        }
        try {
            //call service
            gameService.joinGame(authToken, body.gameID(), body.playerColor());
            // success case
            ctx.status(200);
            ctx.json(Map.of());

        } catch (DataAccessException e) {
            // if already taken, then already taken error
            if (e.getMessage().contains("already taken")) {
                ctx.status(403);
                ctx.json(Map.of("message", "Error: already taken"));
                //game id doesnt exist 400
            } else if (e.getMessage().contains("bad request")) {
                ctx.status(400);
                ctx.json(Map.of("message", "Error: bad request"));
            } else {
                // token is invalid 401
                ctx.status(401);
                ctx.json(Map.of("message", "Error: unauthorized"));
            }
        }
    }
}