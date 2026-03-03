package server.handler;
import service.GameService;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import java.util.Map;
import com.google.gson.Gson;

public class ListHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void list(Context ctx) {
        // get auth token from header
        String authToken = ctx.header("authorization");

        // check if token missing
        //401
        if (authToken == null) {
            ctx.status(401);
            ctx.result("{ \"message\": \"Error: unauthorized\" }");
            return;
        }
        try {
            // get collection of games from service
            var games = gameService.listGames(authToken);
            // success
            ctx.status(200);
            ctx.result(gson.toJson(Map.of("games", games)));
        } catch (DataAccessException e) {
            // if token invalid
            //401
            ctx.status(401);
            ctx.result("{ \"message\": \"Error: unauthorized\" }");
        }
    }
}
