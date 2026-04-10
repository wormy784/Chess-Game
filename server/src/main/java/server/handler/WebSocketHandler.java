package server.handler;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.IAuthDao;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import service.GameService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import io.javalin.websocket.WsContext;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class WebSocketHandler {
    private final Gson gson = new Gson();
    private final Map<Integer, Set<WsContext>> gameSessions = new HashMap<>();
    private final GameService gameService;
    private final IAuthDao authDao;
    public WebSocketHandler(GameService gameService, IAuthDao authDao) {
        this.gameService = gameService;
        this.authDao = authDao;
    }
    private void onConnect(WsConnectContext context) {

    }

    private void onMessage(WsMessageContext context) {
        // desterialize json into UserGameCommand with gson
        var jsonString = (context.message());
        UserGameCommand command = gson.fromJson(jsonString, UserGameCommand.class);



        // switch on commandType and route to other methods
        switch (command.getCommandType()) {
            case CONNECT: handleConnect(context, command);
            break;
            case MAKE_MOVE:
                MakeMoveCommand makeMoveCommand = gson.fromJson(jsonString, MakeMoveCommand.class);
                handleMakeMove(context, makeMoveCommand);
            break;
            case LEAVE: handleLeave(context, command);
            break;
            case RESIGN: handleResign(context, command);
            break;
        }

    }

    private void handleConnect(WsMessageContext context, UserGameCommand command) {
        // add session to map with gameID
        if (gameSessions.containsKey(command.getGameID())) {
            gameSessions.get(command.getGameID());

        } else {
            // new hashset
            gameSessions.put(command.getGameID(), new HashSet<>());
        }
        gameSessions.get(command.getGameID()).add(context);

        // send load game message to client
        try {
            var game = gameService.getGame(command.getAuthToken(), command.getGameID());
            var message = new LoadGameMessage(game);
            var json = gson.toJson(message);
            context.send(json);
            // send notification to other clients
            for (WsContext session : gameSessions.get(command.getGameID())) {
                if (session == context) {
                    continue;
                }
                var username = authDao.getAuth(command.getAuthToken()).username();
                var notifMessage = new NotificationMessage(username + " joined the game");
                var notifJson = gson.toJson(notifMessage);
                session.send(notifJson);
            }
        } catch (DataAccessException e) {
            context.send(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private void handleMakeMove(WsMessageContext context, MakeMoveCommand command) {
            try {
                // verify move
                var updatedGame = gameService.makeMove(command.getAuthToken(), command.getGameID(), command.getMove());
                // update game in database
                var message = new LoadGameMessage(updatedGame);
                var json = gson.toJson(message);
                // username dealio
                var username = authDao.getAuth(command.getAuthToken()).username();
                // load game go to all sessions
                for (WsContext session: gameSessions.get(command.getGameID())) {
                    session.send(json);
                }
                // send notification to other clients
                for (WsContext session : gameSessions.get(command.getGameID())) {

                    var notifMessage = new NotificationMessage(username + "Move Succcess");
                    var notifJson = gson.toJson(notifMessage);
                    // server send Notification message ot all other clients about what move was made
                    if (session != context) {
                        session.send(notifJson);
                    }
                }
                // if move is a check, checkmate or stalemate, server send notification message to all clients
                // white
                if (updatedGame.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
                    var checkMessage = gson.toJson(new NotificationMessage("White is in checkmate!"));
                    for (WsContext s : gameSessions.get(command.getGameID())) {
                        s.send(checkMessage);
                    }
                }
                // white stalemate
                else if (updatedGame.game().isInStalemate(ChessGame.TeamColor.WHITE)) {
                    var checkMessage = gson.toJson(new NotificationMessage("White is in stalemate!"));
                    for (WsContext s : gameSessions.get(command.getGameID())) {
                        s.send(checkMessage);
                    }
                }
                // white in check
                else if (updatedGame.game().isInCheck(ChessGame.TeamColor.WHITE)) {
                    var checkMessage = gson.toJson(new NotificationMessage("White is in check!"));
                    for (WsContext s : gameSessions.get(command.getGameID())) {
                        s.send(checkMessage);
                    }
                }
                //black
                if (updatedGame.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
                    var checkMessage = gson.toJson(new NotificationMessage("BLACK is in checkmate!"));
                    for (WsContext s : gameSessions.get(command.getGameID())) {
                        s.send(checkMessage);
                    }
                }
                // black stalemate
                else if (updatedGame.game().isInStalemate(ChessGame.TeamColor.BLACK)) {
                    var checkMessage = gson.toJson(new NotificationMessage("Black is in stalemate!"));
                    for (WsContext s : gameSessions.get(command.getGameID())) {
                        s.send(checkMessage);
                    }
                }
                // black check
                else if (updatedGame.game().isInCheck(ChessGame.TeamColor.BLACK)) {
                    var checkMessage = gson.toJson(new NotificationMessage("BLACK is in check!"));
                    for (WsContext s : gameSessions.get(command.getGameID())) {
                        s.send(checkMessage);
                    }
                }

            } catch (DataAccessException | InvalidMoveException e) {
                context.send(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
            }

    }

    private void handleLeave(WsMessageContext context, UserGameCommand command) {
        try {
            // remove session from game session
            gameSessions.get(command.getGameID()).remove(context);
            // send notif to all other clients that the player left (or rage-quit)
            for (WsContext session : gameSessions.get(command.getGameID())) {
                var username = authDao.getAuth(command.getAuthToken()).username();
                var notifMessage = new NotificationMessage(username + "has left the game.");
                var notifJson = gson.toJson(notifMessage);
                // server send Notification message ot all other clients about what move was made
                if (session != context) {
                    session.send(notifJson);
                }
            }
        } catch (DataAccessException e) {
            context.send(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private void handleResign(WsMessageContext context, UserGameCommand command) {
        try {
            // mark game as over in db
            var game = gameService.resignGame(command.getAuthToken(), command.getGameID());
            // grab username
            var username = authDao.getAuth(command.getAuthToken()).username();
            // send notif to other clients player resigned (mad cuz bad)
            for (WsContext session : gameSessions.get(command.getGameID())) {

                var notifMessage = new NotificationMessage(username + "has resigned!");
                var notifJson = gson.toJson(notifMessage);
                // server send Notification message ot all other clients about what move was made
                session.send(notifJson);
            }
        } catch (DataAccessException e) {
            context.send(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }

    }
    private void onClose(WsCloseContext context) {
    }
}
