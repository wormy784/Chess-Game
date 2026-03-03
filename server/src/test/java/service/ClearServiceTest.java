package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

public class ClearServiceTest {
    private UserDao userDao;
    private AuthDao authDao;
    private GameDao gameDao;
    private ClearService clearService;

    @BeforeEach
    public void setup() {
        userDao = new UserDao();
        authDao = new AuthDao();
        gameDao = new GameDao();
        clearService = new ClearService(userDao, authDao, gameDao);
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        // create userdata object
        UserData user = new UserData("testuser", "password123", "test@email.com");
                userDao.createUser(user);
                clearService.clear();
                Assertions.assertNull(userDao.getUser("testuser"));
    }


}