package websocket.messages;

public class ErrorMessage extends ServerMessage {
    String errorMessage;
    public ErrorMessage(String errorMessage) {
        super(ServerMessage.ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
}
