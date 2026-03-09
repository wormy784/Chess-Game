package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlDataAccess {

    private final MySqlDataAccess mySqlDataAccess;

    public MySqlDataAccess(MySqlDataAccess mySqlDataAccess) {
        this.mySqlDataAccess = mySqlDataAccess;
    }

    private void configureDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (var statement : mySqlDataAccess.createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(
                    500,
                    String.format("unable to configure database: %s", ex.getMessage())
            );
        }
    }
}