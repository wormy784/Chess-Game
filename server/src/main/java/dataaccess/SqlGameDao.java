package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.Statement;

public class SqlGameDao implements IGameDao{
    // translate Game to sql version

    public int createGame(GameData game) throws DataAccessException {
        // SQL string
        String insert = "INSERT INTO game (whiteUsername, blackUsername, gameName, jsonString) VALUES (?, ?, ?, ?)";
        int ID = 0;
        // open connection
        try (var connection = DatabaseManager.getConnection(); var preparedStatement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setString(3, game.gameName());
            //convert game object to string
            String json = new Gson().toJson(game.game());
            preparedStatement.setString(4, json);

            // prepare statement
            preparedStatement.executeUpdate();
            //ask for generated keys
            try (var rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    ID = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to create Game %s", e.getMessage()));
        }
        return ID;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, jsonString FROM game WHERE gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String json = rs.getString("jsonString");
                        ChessGame game = new Gson().fromJson(json, ChessGame.class);
                        return new GameData(rs.getInt("gameID"), rs.getString( "whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), game);
                    }
                } catch (SQLException ex) {
                    throw new DataAccessException(String.format("Error accessing game data: %s", ex.getMessage()));
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
            var statement = "SELECT * FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String json = rs.getString("jsonString");
                        ChessGame game = new Gson().fromJson(json, ChessGame.class);
                        games.add(new GameData(rs.getInt("gameID"), rs.getString( "whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), game));
                    }
                } catch (SQLException ex) {
                    throw new DataAccessException(String.format("Error accessing game data: %s", ex.getMessage()));
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return games;
    }
    public void updateGame(GameData game) throws DataAccessException{
        var statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, jsonString = ? WHERE gameID = ?";
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

    public void clear() throws DataAccessException {
        // SQL string
        String insert = "TRUNCATE TABLE game";
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
