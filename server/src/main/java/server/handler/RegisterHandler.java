package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.UserService;

public class RegisterHandler {

    private final UserService register;
    private final Gson gson = new Gson();

    public RegisterHandler(UserService register) {
        this.register = register;
    }

    public void register(Context ctx) {
        record RegisterRequest(String username, String password, String email) {}

        var body = gson.fromJson(ctx.body(), RegisterRequest.class);
        // if body null then error
        if (body == null || body.username() == null || body.password() == null || body.email() == null) {
            ctx.status(400);
            ctx.result("{ \"message\": \"Error: bad request\" }");
            return;
        }
        try {
            var auth = register.register(body.username(), body.password(), body.email());
            ctx.status(200);
            ctx.result(gson.toJson(auth));
        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message != null && message.contains("already taken")) {
                ctx.status(403);
            } else if (message != null && message.contains("bad request")) {
                ctx.status(400);
            } else {
                ctx.status(500);
            }
            ctx.result("{ \"message\": \"Error: " + message + "\" }");
        }
    }
}