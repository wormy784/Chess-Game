package websocket.messages;

public class LoadGameMessage extends ServerMessage {
    Object game;
    public LoadGameMessage(Object game) {
        super(ServerMessage.ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
