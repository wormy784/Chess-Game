package server.handler;
import dataaccess.DataAccessException;
import service.ClearService;
import io.javalin.http.Context;

public class CreateGameHandler {

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
        }
    }
}
