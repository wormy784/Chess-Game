package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

public class UserServiceTest {
    private UserDao userDao;
    private AuthDao authDao;
    private GameDao gameDao;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userDao = new UserDao();
        authDao = new AuthDao();
        gameDao = new GameDao();
        userService = new UserService(userDao, authDao);
    }
    @Test
    public void registerSuccess() throws DataAccessException {
        // test register
        AuthData result = userService.register("testuser", "password123", "test@gmail.com");
        Assertions.assertNotNull(userDao.getUser("testuser"));
    }
    @Test
    public void registerUsernameTaken() throws DataAccessException {
        // regester user
        AuthData result = userService.register("testuser", "password123", "test@gmail.com");
        // register again with same name
        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.register("testuser", "password123", "test@gmail.com");
        });
    }
    @Test
    public void loginSuccess() throws DataAccessException {
        // register user
        AuthData result = userService.register("testuser", "password123", "test@gmail.com");
        // login user
        AuthData loginResult = userService.login("testuser", "password123");
        Assertions.assertNotNull(loginResult);
        Assertions.assertEquals("testuser", loginResult.username());
        Assertions.assertNotNull(loginResult.authToken());
    }
    @Test
    public void loginWrongPassword() throws DataAccessException {
        // register user
        AuthData result = userService.register("testuser", "password123", "test@gmail.com");
        // login user
        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.login("testuser", "wrongpassword");
        });
    }
}