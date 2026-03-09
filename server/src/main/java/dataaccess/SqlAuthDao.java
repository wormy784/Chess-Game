package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlAuthDao {
    // translate auth to sql version

    public void createAuth(AuthData auth) throws DataAccessException {
        // SQL string
        String insert = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        // open connection
        try (var connection = DatabaseManager.getConnection(); var preparedStatement = connection.prepareStatement(insert)) {
            preparedStatement.setString(1, auth.authToken());
            preparedStatement.setString(2, auth.username());
            // prepare statement
            preparedStatement.executeUpdate();
            //execute
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to create auth %s", e.getMessage()));
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("authToken"), rs.getString("username"));
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

}
