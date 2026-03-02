package server.handler;

import service.UserService;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
public class LoginHandler {

    private final UserService login;

    public LoginHandler(UserService login) {
        this.login = login;
    }

    public void login(Context ctx) {

        record LoginRequest(String username, String password) {}
        // make sure request not missing fields
        var body = ctx.bodyAsClass(LoginRequest.class);
        if (body.username() == null || body.password() == null) {
            ctx.status(400);
            ctx.json(java.util.Map.of("message", "Error: bad request"));
            return;
        }

        try {
            var auth = login.login(body.username(), body.password());
            ctx.status(200);
            ctx.json(auth);
        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message.contains("unauthorized")) {
                ctx.status(401);
            } else {
                ctx.status(500);
            }
            ctx.json(java.util.Map.of("message", message));
        }
    }
}
