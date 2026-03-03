package server.handler;

import service.UserService;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import com.google.gson.Gson;
public class LoginHandler {

    private final UserService login;
    private final Gson gson = new Gson();

    public LoginHandler(UserService login) {
        this.login = login;
    }

    public void login(Context ctx) {

        record LoginRequest(String username, String password) {}
        // make sure request not missing fields
        var body = gson.fromJson(ctx.body(), LoginRequest.class);

        if (body == null || body.username() == null || body.password() == null) {
            ctx.status(400);
            ctx.result("{ \"message\": \"Error: bad request\" }");            return;
        }

        try {
            var auth = login.login(body.username(), body.password());
            ctx.status(200);
            ctx.result(gson.toJson(auth));
        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message.contains("unauthorized")) {
                ctx.status(401);
            } else {
                ctx.status(500);
            }
            ctx.result("{ \"message\": \"Error: " + message + "\" }");        }
    }
}
