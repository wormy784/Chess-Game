package server.handler;

import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import service.GameService;
import websocket.commands.UserGameCommand;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import io.javalin.websocket.WsContext;

public class WebSocketHandler {
    private final Gson gson = new Gson();
    private final Map<Integer, Set<WsContext>> gameSessions = new HashMap<>();
    private final GameService gameService;
    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }
    private void onConnect(WsConnectContext context) {

    }

    private void onMessage(WsMessageContext context) {
        // desterialize json into UserGameCommand with gson
        var jsonString = (context.message());
        UserGameCommand command = gson.fromJson(jsonString, UserGameCommand.class);


        // switch on commandType and route to other methods
        switch (command.commandType) {
            case CONNECT: handleConnect(context, command);
            break;
            case MAKE_MOVE: break;
            case LEAVE: break;
            case RESIGN: break;
        }

    }

    private void handleConnect(WsMessageContext context, UserGameCommand command) {
        // add session to map with gameID
        if (gameSessions.containsKey(command.gameID)) {
            gameSessions.get(command.gameID);

        } else {
            // new hashset
            gameSessions.put(command.gameID, new HashSet<>());
        }
        gameSessions.get(command.gameID).add(context);

        // send load game message to client
        context.send("Loading");
        // send notification to other clients
        for (WsContext session : gameSessions.get(command.gameID)) {
            if (session == context) {
                continue;
            }
            session.send("Loading"); to each cleint?
        }
    }
    private void onClose(WsCloseContext context) {
    }
}
