package service;

import dataaccess.*;
import model.*;
import java.util.UUID;
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
        UserData existingUser = userDao.getUser(username);
        // check if username exists, throw exception if they do
        if (existingUser != null) {
            throw new DataAccessException("Error: already Taken");
        }
        // create new user
        UserData newUser = new UserData(username, password, email);
        userDao.createUser(newUser);
        //generate auth token
        String authToken = UUID.randomUUID().toString();
        //create authtoken
        AuthData newAuthData = new AuthData(authToken, username);
        authDao.createAuth(newAuthData);
        return newAuthData;
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
