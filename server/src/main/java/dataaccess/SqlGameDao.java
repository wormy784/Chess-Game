package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SqlGameDao {
    // translate Game to sql version

    public void createGame(GameData game) throws DataAccessException {
        // SQL string
        String insert = "INSERT INTO Game (whiteUsername, blackUsername, gameName, jsonString, gameID) VALUES (?, ?, ?, ?)";
        // open connection
        try (var connection = DatabaseManager.getConnection(); var preparedStatement = connection.prepareStatement(insert)) {
            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setString(3, game.gameName());
            //convert game object to string
            String json = new Gson().toJson(game.game());
            preparedStatement.setString(4, json);
            preparedStatement.setInt(4, game.gameID());

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
            throw new DataAccessException(String.format("Unable to get game: %s", e.getMessage()));
        }
        return null;
    }

    public Collection<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM Game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String json = rs.getString("jsonString");
                        ChessGame game = new Gson().fromJson(json, ChessGame.class);
                        games.add(new GameData(rs.getInt("gameID"), rs.getString( "whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), game));
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return games;
    }
    public void updateGame(GameData game) throws DataAccessException{
        var statement = "UPDATE Game SET whiteUsername = ?, blackUsername = ?, jsonString = ? WHERE gameID = ?";
        try (var connection = DatabaseManager.getConnection(); var preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            String json = new Gson().toJson(game.game());
            preparedStatement.setString(3, json);
            preparedStatement.setInt(4, game.gameID());
            // prepare statement
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update game %s", e.getMessage()));
        }
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
