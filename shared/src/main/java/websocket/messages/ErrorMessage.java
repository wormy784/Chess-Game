package websocket.messages;

public class ErrorMessage extends ServerMessage {
    public String errorMessage;
    public ErrorMessage(String errorMessage) {
        super(ServerMessage.ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
}
