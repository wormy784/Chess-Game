package service;
import dataaccess.*;
public class ClearService {
    // add three fields (users, games, authTokens)
    private IUserDao userDao;
    private IAuthDao authDao;
    private IGameDao gameDao;

    //add constructor
    public ClearService(IUserDao userDao, IAuthDao authDao, IGameDao gameDao) {
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
