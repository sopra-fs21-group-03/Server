package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private User testUser;
    private User testUser2;
    private User testUser3;

    private GameEntity testGame;
    //private Optional<GameEntity> option;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername1");
        testUser.setToken("1");
        testUser.setMoney(10);
        testUser.setGamestatus(GameStatus.READY);

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setPassword("testName");
        testUser2.setUsername("testUsername2");
        testUser2.setToken("2");
        testUser2.setMoney(10);
        testUser2.setGamestatus(GameStatus.READY);

        testUser3 = new User();
        testUser3.setId(3L);
        testUser3.setPassword("testName");
        testUser3.setUsername("testUsername3");
        testUser3.setToken("3");
        testUser3.setMoney(10);
        testUser3.setGamestatus(GameStatus.READY);

        testGame = new GameEntity();
        testGame.setGameID(1L);

        ArrayList<User> testActiveUsers = new ArrayList<User>();
        testActiveUsers.add(testUser);
        testActiveUsers.add(testUser2);
        testActiveUsers.add(testUser3);


        ArrayList<User> testAllUsers = new ArrayList<User>();
        testAllUsers.add(testUser);
        testAllUsers.add(testUser2);
        testAllUsers.add(testUser3);

        testGame.setActiveUsers(testActiveUsers);
        testGame.setAllUsers(testAllUsers);

        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(testGame));
    }

    @Test
    void getUserById_success() {
        User myUser = gameService.getUserById(1L,1L);

        assertEquals(myUser.getBlind(), testUser.getBlind());
        assertEquals(myUser.getMoney(), testUser.getMoney());
        assertEquals(myUser.getUsername(), testUser.getUsername());
        assertEquals(myUser.getId(), testUser.getId());
        assertEquals(myUser.getToken(), testUser.getToken());
        assertEquals(myUser.getGamestatus(), testUser.getGamestatus());
    }

    @Test
    void getUserById_fails_userNotFound() {
        //User myUser = gameService.getUserById(1L,4L);
        assertThrows(ResponseStatusException.class, () -> gameService.getUserById(1L,4L));

    }

    @Test
    void userFolds_success() {
       /**
        ArrayList<User> testActiveUsersWithouttestUser1 = new ArrayList<User>();
        testActiveUsersWithouttestUser1.add(testUser2);
        testActiveUsersWithouttestUser1.add(testUser3);*/

        gameService.userFolds(1L,1L);
        assertEquals(2, testGame.getActiveUsers().size());

    }
}






