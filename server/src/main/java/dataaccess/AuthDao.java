package dataaccess;
import java.util.UUID;
import java.util.HashMap;
import model.AuthData;
public class AuthDao {
    //hashmap to store auth data
    private final HashMap<String, AuthData> authTokens = new HashMap<>();

    // create authentication token when user registers or logs in
    void createAuth(AuthData auth) throws DataAccessException {
        authTokens.put(auth.authToken(), auth);
    }

    // retrieve auth data by token
    AuthData getAuth(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    // remove auth token specifically for logout
    void deleteAuth(String authToken) throws DataAccessException {
        authTokens.remove(authToken);
    }

}
