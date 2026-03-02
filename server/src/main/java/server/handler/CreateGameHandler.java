package server.handler;
import dataaccess.DataAccessException;
import service.GameService;
import io.javalin.http.Context;


public class CreateGameHandler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    public void createGame(Context ctx) {
        record CreateGameRequest(String gameName) {}
        // get creategamerequest from ctx
        var body = ctx.bodyAsClass(CreateGameRequest.class);
        // get authorization string from headers
        String authToken = ctx.header("authorization");
        // is token missing
        if (authToken == null) {
            ctx.status(401);
            ctx.json(java.util.Map.of("message", "Error: unauthorized"));
            return;
        }
        // is game name missing
        if (body.gameName() == null) {
            ctx.status(400);
            ctx.json(java.util.Map.of("message", "Error: bad request"));
            return;
        }
        try {
            // service call save id
            int gameID = gameService.createGame(authToken, body.gameName());
            // send success response
            ctx.status(200);
            ctx.json(java.util.Map.of("gameID", gameID));
        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.json(java.util.Map.of("message", "Error: unauthorized"));
        }
    }
}
