package service;
import dataaccess.*;
public class ClearService {
    // add three fields (users, games, authTokens)
    private UserDao userDao;
    private AuthDao authDao;
    private GameDao gameDao;

    //add constructor
    public ClearService(UserDao userDao, AuthDao authDao, GameDao gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    //add clear method
    public void clear() throws DataAccessException {
        // clear on each dao
        userDao.clear();
        authDao.clear();
        gameDao.clear();

    }

}
