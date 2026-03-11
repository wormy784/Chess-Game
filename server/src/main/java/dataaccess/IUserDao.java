package dataaccess;

import model.UserData;

public interface IUserDao {
    void createUser(UserData u) throws DataAccessException;
    boolean verifyUser(String username, String password) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;
}