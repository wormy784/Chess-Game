package server.handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.UserService;

public class RegisterHandler {

    private final UserService register;

    public RegisterHandler(UserService register) {
        this.register = register;
    }

    public void register(Context ctx) {
        record RegisterRequest(String username, String password, String email) {}
        var body = ctx.bodyAsClass(RegisterRequest.class);
        try {
            var auth = register.register(body.username(), body.password(), body.email());
            ctx.status(200);
            ctx.json(java.util.Map.of("username", auth.username(), "authToken", auth.authToken()));
        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message != null && message.contains("already taken")) {
                ctx.status(403);
            } else if (message != null && message.contains("bad request")) {
                ctx.status(400);
            } else {
                ctx.status(500);
            }
            ctx.json(java.util.Map.of("message", message));
        }
    }
}