package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlGameDao {
    // translate Game to sql version

    public void createGame(GameData Game) throws DataAccessException {
        // SQL string
        String insert = "INSERT INTO Game (whiteUsername, blackUsername, gameName, jsonString) VALUES (?, ?, ?, ?)";
        // open connection
        try (var connection = DatabaseManager.getConnection(); var preparedStatement = connection.prepareStatement(insert)) {
            preparedStatement.setString(1, Game.whiteUsername());
            preparedStatement.setString(2, Game.blackUsername());
            preparedStatement.setString(3, Game.gameName());
            //convert game object to string
            String json = new Gson().toJson(Game.game());
            preparedStatement.setString(4, json);
            // prepare statement
            preparedStatement.executeUpdate();
            //execute
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to create Game %s", e.getMessage()));
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, jsonString FROM Game WHERE gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String json = rs.getString("jsonString");
                        ChessGame game = new Gson().fromJson(json, ChessGame.class);
                        return new GameData(rs.getInt("gameID"), rs.getString( "whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), game);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void listGames() {

    }
    public void updateGame(GameData game) {

    }

    public void clearGame() throws DataAccessException {
        // SQL string
        String insert = "TRUNCATE TABLE Game";
        // open connection
        try (var connection = DatabaseManager.getConnection(); var preparedStatement = connection.prepareStatement(insert)) {
            // prepare statement
            preparedStatement.executeUpdate();
            //execute
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to clear Game %s", e.getMessage()));
        }
    }
}
