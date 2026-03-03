package server.handler;

import service.UserService;
import dataaccess.DataAccessException;
import io.javalin.http.Context;

public class LogoutHandler {
    private final UserService logout;

    public LogoutHandler(UserService logout) {
        this.logout = logout;
    }

    public void logout(Context ctx) {
        // grab token
        var token = ctx.header("authorization");
        //check if string is null
        if (token == null) {
            ctx.status(401);
            return;
        }
        try {
            //talk to service
            logout.logout(token);
            //success
            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.result("{ \"message\": \"Error: unauthorized\" }");        }
    }
}
