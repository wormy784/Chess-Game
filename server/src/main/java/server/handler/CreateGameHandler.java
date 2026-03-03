package server.handler;
import dataaccess.DataAccessException;
import service.GameService;
import io.javalin.http.Context;
import com.google.gson.Gson;


public class CreateGameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;

    }
    public void createGame(Context ctx) {
        record CreateGameRequest(String gameName) {}
        // get creategamerequest from ctx
        var body = gson.fromJson(ctx.body(), CreateGameRequest.class);
        // get authorization string from headers
        String authToken = ctx.header("authorization");
        // is token missing 401
        if (body == null || body.gameName() == null) {
            ctx.status(400);
            ctx.result("{ \"message\": \"Error: bad request\" }");
            return;
        }
        // is game name missing 400
        if (authToken == null || authToken.isEmpty()) {
            ctx.status(401);
            ctx.result("{ \"message\": \"Error: unauthorized\" }");
            return;
        }
        try {
            // service call save id
            int gameID = gameService.createGame(authToken, body.gameName());
            // send success response
            ctx.status(200);
            ctx.result(gson.toJson(java.util.Map.of("gameID", gameID)));        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.result("{ \"message\": \"Error: unauthorized\" }");        }
    }
}
