package server.handler;

import service.UserService;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import com.google.gson.Gson;

public class LogoutHandler {
    private final UserService logout;
    private final Gson gson = new Gson();

    public LogoutHandler(UserService logout) {
        this.logout = logout;
    }

    public void logout(Context ctx) {
        // grab token
        String token = ctx.header("authorization");
        //check if string is null
        if (token == null || token.isEmpty()) {
            ctx.status(401);
            ctx.result("{ \"message\": \"Error: unauthorized\" }");
            return;
        }
        try {
            //talk to service
            logout.logout(token);
            //success
            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Error: unauthorized")) {
                ctx.status(401);
                ctx.result("{ \"message\": \"Error: unauthorized\" }");
        } else {
            ctx.status(500);
            ctx.result("{ \"message\": \"Error:" + e.getMessage() + "\" }");
        }
        }
    }
}
