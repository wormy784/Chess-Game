package dataaccess;

import chess.model.UserData;

class UserDao {

    void createUser(UserData u);
    boolean verify(String username, String password);
    UserData getUser(String username);
}