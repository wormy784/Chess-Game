package service;

import dataaccess.*;
import model.*;
public class UserService {
    // add fields for user and authdao
    private UserDao userDao;
    private AuthDao authDao;

    // constructor
    public UserService(UserDao userDao, AuthDao authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    // register
    public AuthData register(String username, String password, String email) throws DataAccessException {

    }
    //login
    public AuthData login(String username, String password) throws DataAccessException {

    }

    // logout
    public void logout(String authToken) throws DataAccessException {
        // check if the token exists
        AuthData auth = authDao.getAuth(authToken);
        // if null then error unauthorized exception
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        // if authdata exists, remove it
        authDao.deleteAuth(authToken);
    }
}
