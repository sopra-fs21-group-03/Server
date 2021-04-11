package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.Pot;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.OnTurnGetDTO;
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
    /**@Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private User testUser;
    private User testUser2;
    private User testUser3;
    private User testUser4;
    private User testUser5;

    private GameEntity testGame;


    int raiseamountpossible;
    int raiseamounttoomuch;

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

        testUser4 = new User();
        testUser4.setId(4L);
        testUser4.setPassword("testName");
        testUser4.setUsername("testUsername4");
        testUser4.setToken("4");
        testUser4.setMoney(10);
        testUser4.setGamestatus(GameStatus.READY);

        testUser5 = new User();
        testUser5.setId(5L);
        testUser5.setPassword("testName");
        testUser5.setUsername("testUsername5");
        testUser5.setToken("5");
        testUser5.setMoney(10);
        testUser5.setGamestatus(GameStatus.READY);

        testGame = new GameEntity();
        testGame.setId(1L);


         //OnTurnGetDTO testOnTurnGetDTO = new OnTurnGetDTO();
         //testOnTurnGetDTO.setUsername("testUsername1");

         //testGame.setOnTurn(testOnTurnGetDTO);

        ArrayList<User> testActiveUsers = new ArrayList<User>();
        testActiveUsers.add(testUser);
        testActiveUsers.add(testUser2);
        testActiveUsers.add(testUser3);
        testActiveUsers.add(testUser4);
        testActiveUsers.add(testUser5);


        ArrayList<User> testAllUsers = new ArrayList<User>();
        testAllUsers.add(testUser);
        testAllUsers.add(testUser2);
        testAllUsers.add(testUser3);
        testAllUsers.add(testUser4);
        testAllUsers.add(testUser5);

        testGame.setActiveUsers(testActiveUsers);
        testGame.setAllUsers(testAllUsers);


        testGame.setCheckcounter(0);



        try {
            testGame.setup();
            OnTurnGetDTO testOnTurnGetDTO = new OnTurnGetDTO();
            testOnTurnGetDTO.setUsername("testUsername1");

            testGame.setOnTurn(testOnTurnGetDTO);
        }
        catch (Exception e) {
            fail();
        }


        raiseamountpossible = 5;
        raiseamounttoomuch = 14000;

        // when -> any object is being save in the gameRepository -> return the dummy testGame
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(testGame));
    }

    @Test
    void getUserById_success() {
        User myUser = gameService.getUserByIdInAllUsers(1L, 1L);

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
        assertThrows(ResponseStatusException.class, () -> gameService.getUserByIdInAllUsers(1L, 6L));

    }

    @Test
    void userFolds_success() {

         //ArrayList<User> testActiveUsersWithouttestUser1 = new ArrayList<User>();
         //testActiveUsersWithouttestUser1.add(testUser2);
         //testActiveUsersWithouttestUser1.add(testUser3);

        gameService.userFolds(1L, 1L);
        assertEquals(4, testGame.getActiveUsers().size());

        for (User user : testGame.getActiveUsers()) {
            if (user == testUser) {
                fail();
            }
        }

    }

    @Test
    void userRaises_success() {
        gameService.userRaises(testGame.getId(), testUser.getId(), raiseamountpossible);
        assertEquals(4995, testUser.getMoney());
        assertEquals(testUser, testGame.getUserThatRaisedLast());
        assertEquals(5, testGame.getPot().getTotal());

        gameService.userRaises(testGame.getId(), testUser2.getId(), raiseamountpossible);
        assertEquals(4995, testUser2.getMoney());
        assertEquals(testUser2, testGame.getUserThatRaisedLast());
        assertEquals(10, testGame.getPot().getTotal());

    }

    @Test
    void userRaises_toohighamount() {
        assertThrows(ResponseStatusException.class, () -> gameService.userRaises(testGame.getId(), testUser.getId(), raiseamounttoomuch));
        assertNotEquals(testUser, testGame.getUserThatRaisedLast());
        assertEquals(0, testGame.getPot().getTotal());

    }

    @Test
    void userRaises_butwasplayerthatraisedlast() {
        gameService.userRaises(testGame.getId(), testUser.getId(), raiseamountpossible);
        assertEquals(5, testUser.getMoney());
        assertEquals(testUser, testGame.getUserThatRaisedLast());
        assertEquals(5, testGame.getPot().getTotal());

        assertThrows(ResponseStatusException.class, () -> gameService.userRaises(testGame.getId(), testUser.getId(), raiseamountpossible));
        assertEquals(5, testUser.getMoney());
        assertEquals(testUser, testGame.getUserThatRaisedLast());
        assertEquals(5, testGame.getPot().getTotal());

    }

    @Test
    void userCalls_success() {
        gameService.userRaises(testGame.getId(), testUser.getId(), raiseamountpossible);
        assertEquals(5, testUser.getMoney());
        assertEquals(testUser, testGame.getUserThatRaisedLast());
        assertEquals(5, testGame.getPot().getTotal());

        gameService.userCalls(testGame.getId(), testUser2.getId());
        assertEquals(5, testUser2.getMoney());
        assertEquals(testUser, testGame.getUserThatRaisedLast());
        assertEquals(10, testGame.getPot().getTotal());
    }

    @Test
    void userCalls_allin_oneplayerhasmoremoney() {
        //testUser is super rich
        testUser.setMoney(50);

        gameService.userRaises(testGame.getId(), testUser.getId(), 45);
        assertEquals(5, testUser.getMoney());
        assertEquals(testUser, testGame.getUserThatRaisedLast());
        assertEquals(45, testGame.getPot().getTotal());

        gameService.userCalls(testGame.getId(), testUser2.getId());
        //testUser2 has to go all-in in order to still be in the game
        assertEquals(0, testUser2.getMoney());
        assertEquals(testUser, testGame.getUserThatRaisedLast());
        assertEquals(55, testGame.getPot().getTotal());
    }

    @Test
    void userchecks_success() {
        gameService.userRaises(testGame.getId(), testUser.getId(), 2);
        gameService.userCalls(testGame.getId(), testUser2.getId());
        gameService.userCalls(testGame.getId(), testUser3.getId());

        gameService.userChecks(testGame.getId(), testUser.getId());
        gameService.userChecks(testGame.getId(), testUser2.getId());
        gameService.userChecks(testGame.getId(), testUser3.getId());

        assertEquals(8, testUser.getMoney());
        assertEquals(8, testUser2.getMoney());
        assertEquals(8, testUser3.getMoney());

        assertEquals(6, testGame.getPot().getTotal());
        assertEquals(testUser, testGame.getUserThatRaisedLast());


    }

    @Test
    void userchecks_youshallnotcheck() {
        gameService.userRaises(testGame.getId(), testUser.getId(), 2);

        assertThrows(ResponseStatusException.class, () -> gameService.userChecks(testGame.getId(), testUser2.getId()));
        assertThrows(ResponseStatusException.class, () -> gameService.userChecks(testGame.getId(), testUser3.getId()));

        assertEquals(4998, testUser.getMoney());
        assertEquals(5000, testUser2.getMoney());
        assertEquals(5000, testUser3.getMoney());

        assertEquals(2, testGame.getPot().getTotal());
        assertEquals(testUser, testGame.getUserThatRaisedLast());


    }*/
}







