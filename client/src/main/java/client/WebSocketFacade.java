package client;

import com.google.gson.Gson;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;

import java.net.URI;

@ClientEndpoint
public class WebSocketFacade {
    private MessageHandler messageHandler;
    private Session session;


    public WebSocketFacade(String serverUrl, MessageHandler messageHandler) throws Exception {
        this.messageHandler = messageHandler;
        var container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, URI.create(serverUrl));
    }


    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        messageHandler.onMessage(message);
    }

    public void sendMessage(UserGameCommand command) throws Exception {
        var json = new Gson().toJson(command);
        session.getBasicRemote().sendText(json);
    }

}
