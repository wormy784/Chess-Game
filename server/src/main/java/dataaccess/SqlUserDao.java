package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SqlUserDao implements IUserDao {
    // translate user to sql version

    public boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
        // read the previously hashed password from the database
        UserData hashedPassword = getUser(username);
        if (hashedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword.password());
    }

    public void createUser(UserData user) throws DataAccessException {
        // SQL string
        String insert = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        // open connection
        try (var connection = DatabaseManager.getConnection(); var preparedStatement = connection.prepareStatement(insert)) {
            preparedStatement.setString(1, user.username());
            // write the hashed password in database along with the user's other information
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, user.email());
            // prepare statement
            preparedStatement.executeUpdate();
            //execute
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to create user %s", e.getMessage()));
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                    }
                } catch (SQLException ex) {
                    throw new DataAccessException(String.format("Error finding user %s: %s", username, ex.getMessage()));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void clear() throws DataAccessException {
        // SQL string
        String insert = "TRUNCATE TABLE user";
        // open connection
        try (var connection = DatabaseManager.getConnection(); var preparedStatement = connection.prepareStatement(insert)) {
            // prepare statement
            preparedStatement.executeUpdate();
            //execute
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to clear user %s", e.getMessage()));
        }
    }


}
