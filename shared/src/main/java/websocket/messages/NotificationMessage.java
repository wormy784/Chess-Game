package websocket.messages;

public class NotificationMessage extends ServerMessage {
    public String message;
    public NotificationMessage(String message) {
        super(ServerMessage.ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}
