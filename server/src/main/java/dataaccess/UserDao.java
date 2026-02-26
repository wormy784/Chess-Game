package dataaccess;

import model.UserData;

import java.util.HashMap;

public class UserDao {

    private final HashMap<String, UserData> users = new HashMap<>();
    public void createUser(UserData u) throws DataAccessException{
        users.put(u.username(), u);
    }

    public boolean verify(String username, String password) throws DataAccessException{
        // get user
        var user = getUser(username);
        //check if user exists
        if (user == null) {
           return false;
        }
        // check if the passwords match
            return user.password().equals(password);
    }

    public UserData getUser(String username) throws DataAccessException{
        // find userdata by username
        return users.get(username);
    }

    public void clear() throws DataAccessException {
        // clear the hashmap
        users.clear();
    }
}