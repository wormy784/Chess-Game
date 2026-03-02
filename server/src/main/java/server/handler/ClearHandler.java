package server.handler;

import dataaccess.DataAccessException;
import service.ClearService;
import io.javalin.http.Context;

public class ClearHandler {
    private ClearService clearService;
    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }
    public void clear(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(java.util.Map.of("message", e.getMessage()));
        }
    }
}
