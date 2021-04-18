package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.Blind;
import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs21.constant.Round;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
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

    private User getBigBlind() {
        User bigblind = null;
        for (User user : testGame.getActiveUsers()) {
            if (user.getBlind() == Blind.BIG) {
                bigblind = user;
                break;
            }
        }
        return bigblind;
    }

    private User getSmallBlind() {
        User smallblind = null;
        for (User user : testGame.getActiveUsers()) {
            if (user.getBlind() == Blind.SMALL) {
                smallblind = user;
                break;
            }
        }
        return smallblind;
    }

    private Long getIdOfUserOnTurn() {
        String username = testGame.getOnTurn().getUsername();
        Long id = 1L;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username)) {
                id = user.getId();
            }
        }
        return id;
    }

    private User getOnTurnUser() {
        String username = testGame.getOnTurn().getUsername();
        User theuser = null;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username)) {
                theuser = user;
                break;
            }
        }
        return theuser;
    }


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
        Mockito.when(gameRepository.findById(testGame.getId())).thenReturn(Optional.ofNullable(testGame));
    }


    /**
     * Player got his own gameData
     */
    @Test
    void getOwnGameData_success() {
        User mock = gameService.getOwnGameData(testGame.getId(), testUser.getId(), testUser);

        assertEquals(testUser, mock);
    }

    /**
     * Player could not be found
     */
    @Test
    void getOwnGameData_notFound() {
        assertThrows(ResponseStatusException.class,
                () -> gameService.getOwnGameData(testGame.getId(), testUser.getId() + 1, testUser));
    }

    /**
     * Player is not authorized to get this data
     * In this case testUser tries to access testUser2s gameData
     */
    @Test
    void getOwnGameData_unauthorized() {
        assertThrows(ResponseStatusException.class,
                () -> gameService.getOwnGameData(testGame.getId(), testUser2.getId(), testUser));
    }

    /**
     * Tests if the service returns the right gameEntity
     * Also implicitly tests if game gets set Upped correctly
     */
    @Test
    void getGameData_success() {
        GameEntity mock = gameService.getGameData(testGame.getId(), testUser);

        assertEquals(mock, testGame);
    }

    /**
     * Tests if the right exception is thrown when a user tries to access a nonexistent game
     */
    @Test
    void getGameData_notFound() {
        assertThrows(ResponseStatusException.class, () -> gameService.getGameData(2L, testUser));
    }

    /**
     * User is not authorized to get gameData
     */
    @Test
    void getGameData_unauthorized() {
        User falseTokenUser = new User();
        falseTokenUser.setToken("falseToken");

        assertThrows(ResponseStatusException.class, () -> gameService.getGameData(testGame.getId(), falseTokenUser));
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
        Long id = getIdOfUserOnTurn();
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
        Long id = getIdOfUserOnTurn();
        User user = getOnTurnUser();

        gameService.userCallsForRaising(testGame.getId(), id);
        gameService.userRaises(testGame.getId(), id, raiseamountpossible);

        assertEquals(4795, user.getMoney());
        assertEquals(user.getUsername(), testGame.getUserThatRaisedLast().getUsername());
        assertEquals(505, testGame.getPot().getTotal());

        id = getIdOfUserOnTurn();
        user = getOnTurnUser();

        gameService.userCallsForRaising(testGame.getId(), id);
        gameService.userRaises(testGame.getId(), id, raiseamountpossible);

        assertEquals(4790, user.getMoney());
        assertEquals(user.getUsername(), testGame.getUserThatRaisedLast().getUsername());
        assertEquals(715, testGame.getPot().getTotal());

    }

    @Test
    void userRaises_toohighamount() {

        Long id = getIdOfUserOnTurn();

        gameService.userCallsForRaising(testGame.getId(), id);
        assertThrows(ResponseStatusException.class, () -> gameService.userRaises(testGame.getId(), id, raiseamounttoomuch));
        assertNotEquals(id, testGame.getUserThatRaisedLast().getId());
        assertEquals(500, testGame.getPot().getTotal());

    }

    @Test
    void userRaises_butwasplayerthatraisedlast() {
        User user = getOnTurnUser();
        String username = user.getUsername();
        Long id = getIdOfUserOnTurn();

        gameService.userCallsForRaising(testGame.getId(), id);
        gameService.userRaises(testGame.getId(), id, raiseamountpossible);

        assertEquals(4795, user.getMoney());
        assertEquals(user.getUsername(), testGame.getUserThatRaisedLast().getUsername());
        assertEquals(505, testGame.getPot().getTotal());
        assertThrows(ResponseStatusException.class, () -> gameService.userCallsForRaising(testGame.getId(), id));
        assertThrows(ResponseStatusException.class, () -> gameService.userRaises(testGame.getId(), id, raiseamountpossible));

        for (User aUser : testGame.getActiveUsers()) {
            if (aUser.getUsername().equals(username)) {
                assertEquals(4795, aUser.getMoney());
                assertEquals(aUser.getUsername(), testGame.getUserThatRaisedLast().getUsername());
            }
        }

        assertEquals(505, testGame.getPot().getTotal());

    }

    @Test
    void userCalls_success() {
        User myUser = getOnTurnUser();
        String username = myUser.getUsername();
        Long id = getIdOfUserOnTurn();

        gameService.userCallsForRaising(testGame.getId(), id);
        gameService.userRaises(testGame.getId(), id, raiseamountpossible);

        assertEquals(4795, myUser.getMoney());
        assertEquals(myUser.getUsername(), testGame.getUserThatRaisedLast().getUsername());
        assertEquals(505, testGame.getPot().getTotal());

        myUser = getOnTurnUser();
        String username2 = myUser.getUsername();
        Long id2 = getIdOfUserOnTurn();

        gameService.userCalls(testGame.getId(), id2);
        assertEquals(4795, myUser.getMoney());
        assertEquals(id, testGame.getUserThatRaisedLast().getId());
        assertEquals(710, testGame.getPot().getTotal());
    }

    @Test
    void userCalls_firstUserOnTurn_nooneraised_onlysmallandbigblindputtheirinput_success() {
        User onTurn = getOnTurnUser();
        Long id2 = getIdOfUserOnTurn();
        gameService.userCalls(testGame.getId(), id2);
        assertEquals(4800, onTurn.getMoney());
        assertEquals(500, testGame.getPot().getTotal());
    }

    @Test
    void userchecks_success() {
        //for checking, we are going to be in the FLOP round
        int counter = 0;
        while (counter < 4) {
            Long id = getIdOfUserOnTurn();
            gameService.userCalls(testGame.getId(), id);
            counter++;
        }
        //now we should be in the FLOP round -> everyone will check now (The Test should test if this works)
        assertEquals(Round.FLOP, testGame.getRound());

        counter = 0;
        while (counter < 5) {
            Long id = getIdOfUserOnTurn();
            gameService.userChecks(testGame.getId(), id);
            counter++;
        }
        for (User user : testGame.getActiveUsers()) {
            assertEquals(200, testGame.getPot().getUserContributionOfAUser(user));
            assertEquals(4800, user.getMoney());
        }

        assertEquals(1000, testGame.getPot().getTotal());
    }

    @Test
    void userChecks_youShallNotCheck() {
        Long id2 = getIdOfUserOnTurn();
        User theUser = getOnTurnUser();

        assertThrows(ResponseStatusException.class, () -> gameService.userChecks(testGame.getId(), id2));
        assertEquals(5000, theUser.getMoney());
        assertEquals(theUser.getUsername(), testGame.getOnTurn().getUsername());
        assertEquals(300, testGame.getPot().getTotal());

    }

    @Test
    void allUsersExceptOneFold_oneClearWinner_testingWithPreFlop() {
        User bigblind = getBigBlind();

        int counter = 0;
        while (counter < 4) {
            gameService.userFolds(testGame.getId(), getIdOfUserOnTurn());
            counter++;
        }

        assertEquals(Round.PREFLOP, testGame.getRound());
        assertEquals(300, testGame.getPot().getTotal());
        assertEquals(5000, bigblind.getMoney());
    }

    @Test
    void allUsersExceptOneFold_oneClearWinner_testingWithTurncard() {
        User smallBlind = getSmallBlind();

        int counter = 0;
        while (counter < 4) {
            gameService.userCalls(testGame.getId(), getIdOfUserOnTurn());
            counter++;
        }
        //Now, 200 * 5 = 1000 are in the pot
        //at the beginning of each new round (not being Preflop), the small blind starts

        assertEquals(Round.FLOP, testGame.getRound());
        assertEquals(1000, testGame.getPot().getTotal());

        counter = 0;
        while (counter < 5) {
            gameService.userChecks(testGame.getId(), getIdOfUserOnTurn());
            counter++;
        }
        counter = 0;

        assertEquals(Round.TURNCARD, testGame.getRound());
        assertEquals(1000, testGame.getPot().getTotal());

        gameService.userChecks(testGame.getId(), getIdOfUserOnTurn());
        while (counter < 4) {
            gameService.userFolds(testGame.getId(), getIdOfUserOnTurn());
            counter++;
        }

        assertEquals(Round.PREFLOP, testGame.getRound());
        assertEquals(300, testGame.getPot().getTotal());
        assertEquals(5800, smallBlind.getMoney());
    }

    @Test
    void playerFolds_nextUserComesInTurn() {
        User onTurn = getOnTurnUser();
        int indexOfonTurn = testGame.getActiveUsers().indexOf(onTurn);
        int indexOfUserAfteronTurn = Math.abs((indexOfonTurn - 1 + testGame.getAllUsers().size()) % (testGame.getAllUsers().size()));
        User userAfteronTurn = testGame.getActiveUsers().get(indexOfUserAfteronTurn);

        gameService.userFolds(testGame.getId(), onTurn.getId());
        assertEquals(userAfteronTurn.getUsername(), testGame.getOnTurn().getUsername());
    }

    @Test
    void playerRaises_nextUserComesInTurn() {
        User onTurn = getOnTurnUser();
        int indexOfonTurn = testGame.getActiveUsers().indexOf(onTurn);
        int indexOfUserAfteronTurn = Math.abs((indexOfonTurn - 1 + testGame.getAllUsers().size()) % (testGame.getAllUsers().size()));
        User userAfteronTurn = testGame.getActiveUsers().get(indexOfUserAfteronTurn);

        gameService.userRaises(testGame.getId(), onTurn.getId(), 300);
        assertEquals(userAfteronTurn.getUsername(), testGame.getOnTurn().getUsername());
    }

    @Test
    void playerCalls_nextUserComesInTurn() {
        User onTurn = getOnTurnUser();
        int indexOfonTurn = testGame.getActiveUsers().indexOf(onTurn);
        int indexOfUserAfteronTurn = Math.abs((indexOfonTurn - 1 + testGame.getAllUsers().size()) % (testGame.getAllUsers().size()));
        User userAfteronTurn = testGame.getActiveUsers().get(indexOfUserAfteronTurn);

        gameService.userCalls(testGame.getId(), onTurn.getId());
        assertEquals(userAfteronTurn.getUsername(), testGame.getOnTurn().getUsername());
    }

    @Test
    void playerChecks_nextUserComesInTurn() {
        int counter = 0;
        while (counter < 4) {
            gameService.userCalls(testGame.getId(), getIdOfUserOnTurn());
            counter++;
        }

        User onTurn = getOnTurnUser();
        int indexOfonTurn = testGame.getActiveUsers().indexOf(onTurn);
        int indexOfUserAfteronTurn = Math.abs((indexOfonTurn - 1 + testGame.getAllUsers().size()) % (testGame.getAllUsers().size()));
        User userAfteronTurn = testGame.getActiveUsers().get(indexOfUserAfteronTurn);

        gameService.userChecks(testGame.getId(), onTurn.getId());
        assertEquals(userAfteronTurn.getUsername(), testGame.getOnTurn().getUsername());
    }

    @Test
    void playerWhoRaisedLastIsReached_NextRoundShouldStart(){
        int counter = 0;
        while (counter < 4) {
            gameService.userCalls(testGame.getId(), getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(Round.FLOP, testGame.getRound());
        gameService.userRaises(testGame.getId(), getIdOfUserOnTurn(), 500);

        counter = 0;
        while (counter < 4) {
            gameService.userCalls(testGame.getId(), getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(Round.TURNCARD, testGame.getRound());
    }

    @Test
    void playerWhoStartedTheRoundIsReachedAgain_EveryoneChecked_NextRoundShouldStart(){
        int counter = 0;
        while (counter < 4) {
            gameService.userCalls(testGame.getId(), getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(Round.FLOP, testGame.getRound());
        counter = 0;
        while (counter < 5) {
            gameService.userChecks(testGame.getId(), getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(Round.TURNCARD, testGame.getRound());
    }

}







