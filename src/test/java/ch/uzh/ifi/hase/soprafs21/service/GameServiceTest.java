package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs21.constant.Round;
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
    @Mock
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

        assertThrows(ResponseStatusException.class, () -> gameService.getUserByIdInAllUsers(1L, 6L));

    }

    @Test
    void userFolds_success() {
        String username = testGame.getOnTurn().getUsername();
        Long id = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username)) {
                id = user.getId();
            }
        }

        assertNotNull(id);
        gameService.userFolds(1L, id);
        assertEquals(4, testGame.getActiveUsers().size());

        for (User user : testGame.getActiveUsers()) {
            if (user.getId() == id) {
                fail();
            }
        }

    }

    @Test
    void userRaises_success() {

        String username = testGame.getOnTurn().getUsername();
        Long id = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username)) {
                id = user.getId();
            }
        }
        gameService.userCallsForRaising(testGame.getId(), id);
        gameService.userRaises(testGame.getId(), id, raiseamountpossible);

        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username)) {
                assertEquals(4795, user.getMoney());
                assertEquals(user.getUsername(), testGame.getUserThatRaisedLast().getUsername());
            }
        }

        assertEquals(505, testGame.getPot().getTotal());

        String username2 = testGame.getOnTurn().getUsername();

        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                id = user.getId();
            }
        }
        gameService.userCallsForRaising(testGame.getId(), id);
        gameService.userRaises(testGame.getId(), id, raiseamountpossible);

        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                assertEquals(4790, user.getMoney());
                assertEquals(user.getUsername(), testGame.getUserThatRaisedLast().getUsername());
            }
        }


        assertEquals(715, testGame.getPot().getTotal());

    }

    @Test
    void userRaises_toohighamount() {
        String username = testGame.getOnTurn().getUsername();
        Long id = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username)) {
                id = user.getId();
            }
        }

        Long finalId = id;
        gameService.userCallsForRaising(testGame.getId(), id);
        assertThrows(ResponseStatusException.class, () -> gameService.userRaises(testGame.getId(), finalId, raiseamounttoomuch));
        assertNotEquals(id, testGame.getUserThatRaisedLast().getId());
        assertEquals(500, testGame.getPot().getTotal());

    }

    @Test
    void userRaises_butwasplayerthatraisedlast() {
        String username = testGame.getOnTurn().getUsername();
        Long id = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username)) {
                id = user.getId();
            }
        }
        gameService.userCallsForRaising(testGame.getId(), id);
        gameService.userRaises(testGame.getId(), id, raiseamountpossible);
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username)) {
                assertEquals(4795, user.getMoney());
                assertEquals(user.getUsername(), testGame.getUserThatRaisedLast().getUsername());
            }
        }

        assertEquals(505, testGame.getPot().getTotal());

        Long finalId = id;
        assertThrows(ResponseStatusException.class, () -> gameService.userCallsForRaising(testGame.getId(), finalId));
        assertThrows(ResponseStatusException.class, () -> gameService.userRaises(testGame.getId(), finalId, raiseamountpossible));
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username)) {
                assertEquals(4795, user.getMoney());
                assertEquals(user.getUsername(), testGame.getUserThatRaisedLast().getUsername());
            }
        }

        assertEquals(505, testGame.getPot().getTotal());

    }

    @Test
    void userCalls_success() {
        String username = testGame.getOnTurn().getUsername();
        Long id = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username)) {
                id = user.getId();
            }
        }
        gameService.userCallsForRaising(testGame.getId(), id);
        gameService.userRaises(testGame.getId(), id, raiseamountpossible);
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username)) {
                assertEquals(4795, user.getMoney());
                assertEquals(user.getUsername(), testGame.getUserThatRaisedLast().getUsername());
            }
        }

        assertEquals(505, testGame.getPot().getTotal());
        String username2 = testGame.getOnTurn().getUsername();
        Long id2 = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                id2 = user.getId();
            }
        }
        gameService.userCalls(testGame.getId(), id2);

        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                assertEquals(4795, user.getMoney());
            }
        }

        assertEquals(id, testGame.getUserThatRaisedLast().getId());
        assertEquals(710, testGame.getPot().getTotal());
    }

    @Test
    void userCalls_firstUserOnTurn_nooneraised_onlysmallandbigblindputtheirinput_success() {

        String username2 = testGame.getOnTurn().getUsername();
        Long id2 = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                id2 = user.getId();
            }
        }
        gameService.userCalls(testGame.getId(), id2);

        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                assertEquals(4800, user.getMoney());
            }
        }
        assertEquals(500, testGame.getPot().getTotal());
    }

    /**
     * @Test void userCalls_allin_oneplayerhasmoremoney() {
     * //testUser is super rich
     * testUser.setMoney(50);
     * <p>
     * gameService.userRaises(testGame.getId(), testUser.getId(), 45);
     * assertEquals(5, testUser.getMoney());
     * assertEquals(testUser, testGame.getUserThatRaisedLast());
     * assertEquals(45, testGame.getPot().getTotal());
     * <p>
     * gameService.userCalls(testGame.getId(), testUser2.getId());
     * //testUser2 has to go all-in in order to still be in the game
     * assertEquals(0, testUser2.getMoney());
     * assertEquals(testUser, testGame.getUserThatRaisedLast());
     * assertEquals(55, testGame.getPot().getTotal());
     * }
     */

    @Test
    void userchecks_success() {
        //for checking, we are going to be in the FLOP round
        String username2 = testGame.getOnTurn().getUsername();
        Long id2 = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                id2 = user.getId();
            }
        }
        gameService.userCalls(testGame.getId(), id2);

        username2 = testGame.getOnTurn().getUsername();
        id2 = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                id2 = user.getId();
            }
        }
        gameService.userCalls(testGame.getId(), id2);

        username2 = testGame.getOnTurn().getUsername();
        id2 = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                id2 = user.getId();
            }
        }
        gameService.userCalls(testGame.getId(), id2);

        username2 = testGame.getOnTurn().getUsername();
        id2 = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                id2 = user.getId();
            }
        }
        gameService.userCalls(testGame.getId(), id2);

        //now we should be in the FLOP round -> everyone will check now (The Test should test if this works)
        assertEquals(Round.FLOP, testGame.getRound());

        username2 = testGame.getOnTurn().getUsername();
        id2 = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                id2 = user.getId();
            }
        }
        gameService.userChecks(testGame.getId(), id2);

        username2 = testGame.getOnTurn().getUsername();
        id2 = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                id2 = user.getId();
            }
        }
        gameService.userChecks(testGame.getId(), id2);

        username2 = testGame.getOnTurn().getUsername();
        id2 = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                id2 = user.getId();
            }
        }
        gameService.userChecks(testGame.getId(), id2);

        username2 = testGame.getOnTurn().getUsername();
        id2 = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                id2 = user.getId();
            }
        }
        gameService.userChecks(testGame.getId(), id2);

        username2 = testGame.getOnTurn().getUsername();
        id2 = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                id2 = user.getId();
            }
        }
        gameService.userChecks(testGame.getId(), id2);

        for(User user: testGame.getActiveUsers()){
            assertEquals(200, testGame.getPot().getUserContributionOfAUser(user));
            assertEquals(4800, user.getMoney());
        }

        assertEquals(1000, testGame.getPot().getTotal());
    }

    @Test
    void userchecks_youshallnotcheck() {
        String username2 = testGame.getOnTurn().getUsername();
        Long id2 = 1L;
        User theUser = new User();
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username2)) {
                id2 = user.getId();
                theUser = user;
                break;
            }
        }

        Long finalId = id2;
        assertThrows(ResponseStatusException.class, () -> gameService.userChecks(testGame.getId(), finalId));
        assertEquals(5000, theUser.getMoney());
        assertEquals(theUser.getUsername(), testGame.getOnTurn().getUsername());
        assertEquals(300, testGame.getPot().getTotal());

    }
}







