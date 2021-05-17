package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.*;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.cards.Deck;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.PlayerInGameGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GameService gameService;


    private User testUser;
    private User testUser2;
    private User testUser3;

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

        var testUser4 = new User();
        testUser4.setId(4L);
        testUser4.setPassword("testName");
        testUser4.setUsername("testUsername4");
        testUser4.setToken("4");
        testUser4.setMoney(10);
        testUser4.setGamestatus(GameStatus.READY);

        var testUser5 = new User();
        testUser5.setId(5L);
        testUser5.setPassword("testName");
        testUser5.setUsername("testUsername5");
        testUser5.setToken("5");
        testUser5.setMoney(10);
        testUser5.setGamestatus(GameStatus.READY);

        testGame = new GameEntity(1L);
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
     * fetch game data no one shows cards
     * Example in game: No one has decided yet
     */
    @Test
    void getDataDuringShowdown_success_nobodyHasDecidedYet() {
        testGame.setShowdown(true);
        List<User> gameEntityPlayers = new ArrayList<>(testGame.getAllUsers());
        List<PlayerInGameGetDTO> players = gameService.getDataDuringShowdown(testGame.getId(), testUser);

        int idx = 0;

        for (PlayerInGameGetDTO player : players) {
            assertEquals(player.getBlind(), gameEntityPlayers.get(idx).getBlind());
            assertEquals(player.getCards(), new ArrayList<>());
            assertEquals(player.getMoney(), gameEntityPlayers.get(idx).getMoney());
            assertEquals(player.getUsername(), gameEntityPlayers.get(idx).getUsername());

            idx++;
        }
    }


    /**
     * Every user doesn't want to show his cards during showdown
     */
    @Test
    void getDataDuringShowdown_success_nobodyWantsToShow() {
        testGame.setShowdown(true);
        for (User player : testGame.getAllUsers()) {
            player.setWantsToShow(Show.DONT_SHOW);
        }

        List<User> gameEntityPlayers = new ArrayList<>(testGame.getAllUsers());
        List<PlayerInGameGetDTO> players = gameService.getDataDuringShowdown(testGame.getId(), testUser);

        int idx = 0;

        for (PlayerInGameGetDTO player : players) {
            assertEquals(player.getBlind(), gameEntityPlayers.get(idx).getBlind());
            assertEquals(player.getCards(), new ArrayList<>());
            assertEquals(player.getMoney(), gameEntityPlayers.get(idx).getMoney());
            assertEquals(player.getUsername(), gameEntityPlayers.get(idx).getUsername());

            idx++;
        }

    }

    /**
     * Mixed decisions only testUser and testUser3 want to show cards
     */
    @Test
    void getDataDuringShowdown_success_mixedDecisions() {
        testGame.setShowdown(true);
        testUser.setWantsToShow(Show.SHOW);
        testUser3.setWantsToShow(Show.SHOW);

        List<User> gameEntityPlayers = new ArrayList<>(testGame.getAllUsers());
        List<PlayerInGameGetDTO> players = gameService.getDataDuringShowdown(testGame.getId(), testUser);

        int idx = 0;

        for (PlayerInGameGetDTO player : players) {
            if (player.getUsername().equals(testUser.getUsername())) {
                assertEquals(player.getBlind(), testUser.getBlind());
                assertEquals(player.getMoney(), testUser.getMoney());
                assertEquals(player.getCards(), testUser.getCards());
            }
            else if (player.getUsername().equals(testUser3.getUsername())) {
                assertEquals(player.getBlind(), testUser3.getBlind());
                assertEquals(player.getMoney(), testUser3.getMoney());
                assertEquals(player.getCards(), testUser3.getCards());
            }
            else {
                assertEquals(player.getBlind(), gameEntityPlayers.get(idx).getBlind());
                assertEquals(player.getCards(), new ArrayList<>());
                assertEquals(player.getMoney(), gameEntityPlayers.get(idx).getMoney());
                assertEquals(player.getUsername(), gameEntityPlayers.get(idx).getUsername());
            }
            idx++;
        }
    }

    /**
     * fetch game data everyone shows cards
     */
    @Test
    void getDataDuringShowdown_success_everyoneShowsCards() {
        testGame.setShowdown(true);
        for (User player : testGame.getAllUsers()) {
            player.setWantsToShow(Show.SHOW);
        }
        List<User> gameEntityPlayers = new ArrayList<>(testGame.getAllUsers());
        List<PlayerInGameGetDTO> players = gameService.getDataDuringShowdown(testGame.getId(), testUser);

        int idx = 0;

        for (PlayerInGameGetDTO player : players) {
            assertEquals(player.getBlind(), gameEntityPlayers.get(idx).getBlind());
            assertEquals(player.getCards(), gameEntityPlayers.get(idx).getCards());
            assertEquals(player.getMoney(), gameEntityPlayers.get(idx).getMoney());
            assertEquals(player.getUsername(), gameEntityPlayers.get(idx).getUsername());

            idx++;
        }

    }

    /**
     * game not found when accessing data
     */
    @Test
    void getDataDuringShowdown_notFound() {
        testGame.setShowdown(true);
        assertThrows(ResponseStatusException.class,
                () -> gameService.getDataDuringShowdown(testGame.getId() + 1, testUser));

    }

    /**
     * Unauthorized user tries to get data during showdown
     */
    @Test
    void getDataDuringShowdown_unauthorized() {
        testGame.setShowdown(true);
        User unauthorized = new User();
        unauthorized.setToken("falseToken");

        assertThrows(ResponseStatusException.class,
                () -> gameService.getDataDuringShowdown(testGame.getId(), unauthorized));
    }

    /**
     * Try to access showdown data while game is not yet in showdown
     */
    @Test
    void getDataDuringShowdown_conflict() {
        assertThrows(ResponseStatusException.class,
                () -> gameService.getDataDuringShowdown(testGame.getId(), testUser));
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
    void getGameEntity_NotFound() {
        assertThrows(ResponseStatusException.class, () -> gameService.findGameEntity(7L));

    }

    @Test
    void getUserByIdInActiveUsers_HeWasNotFound() {
        assertThrows(ResponseStatusException.class, () -> gameService.getUserByIdInActiveUsers(testGame, 55L));
    }


    @Test
    void getUserById_success() {
        User myUser = gameService.getUserByIdInAllUsers(testGame, 1L);

        assertEquals(myUser.getBlind(), testUser.getBlind());
        assertEquals(myUser.getMoney(), testUser.getMoney());
        assertEquals(myUser.getUsername(), testUser.getUsername());
        assertEquals(myUser.getId(), testUser.getId());
        assertEquals(myUser.getToken(), testUser.getToken());
        assertEquals(myUser.getGamestatus(), testUser.getGamestatus());
    }

    @Test
    void getUserById_fails_userNotFound() {
        assertThrows(ResponseStatusException.class, () -> gameService.getUserByIdInAllUsers(testGame, 6L));
    }

    @Test
    void userFolds_success() {
        Long id = getIdOfUserOnTurn();
        assertNotNull(id);
        gameService.userFolds(testGame, id);
        assertEquals(4, testGame.getActiveUsers().size());

        for (User user : testGame.getActiveUsers()) {
            if (user.getId() == id) {
                fail();
            }
        }

    }

    @Test
    void userFolds_notFound() {
        assertThrows(ResponseStatusException.class, () -> gameService.userFolds(testGame, 6L));
    }

    @Test
    void userFolds_notOnTurn() {
        Long idOfUserNotOnTurn = ((getIdOfUserOnTurn() + 1) % testGame.getAllUsers().size()) + 1;
        assertThrows(ResponseStatusException.class, () -> gameService.userFolds(testGame, idOfUserNotOnTurn));
    }

    @Test
    void userFolds_nextUserComesInTurn() {
        User onTurn = getOnTurnUser();
        int indexOfonTurn = testGame.getActiveUsers().indexOf(onTurn);
        int indexOfUserAfteronTurn = Math.abs((indexOfonTurn - 1 + testGame.getAllUsers().size()) % (testGame.getAllUsers().size()));
        User userAfteronTurn = testGame.getActiveUsers().get(indexOfUserAfteronTurn);

        gameService.userFolds(testGame, onTurn.getId());
        assertEquals(userAfteronTurn.getUsername(), testGame.getOnTurn().getUsername());
    }

    @Test
    void userFolds_isSmallBlind_specialCaseForTheFollowingRounds() {
        User smallblind = getSmallBlind();
        User bigblind = getBigBlind();
        int counter = 0;
        while (counter < 3) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        //Now: it's the turn of the Small Blind -> he folds
        gameService.userFolds(testGame, getIdOfUserOnTurn());
        gameService.userCalls(testGame, getIdOfUserOnTurn());
        assertEquals(Round.FLOP, testGame.getRound());
        assertFalse(testGame.getActiveUsers().contains(smallblind));
        assertEquals(getIdOfUserOnTurn(), bigblind.getId());
        gameService.userCalls(testGame, getIdOfUserOnTurn());

    }

    @Test
    void userFolds_isSmallBlind_specialCaseForTheFollowingRounds_allTheOthersGoAllIn() {
        User smallblind = getSmallBlind();
        User bigblind = getBigBlind();
        int counter = 0;
        while (counter < 3) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        //Now: it's the turn of the Small Blind -> he folds
        gameService.userFolds(testGame, getIdOfUserOnTurn());
        gameService.userCalls(testGame, getIdOfUserOnTurn());
        assertEquals(Round.FLOP, testGame.getRound());
        assertFalse(testGame.getActiveUsers().contains(smallblind));
        assertEquals(getIdOfUserOnTurn(), bigblind.getId());
        gameService.userRaises(testGame, getIdOfUserOnTurn(), 4800);
        counter = 0;
        while (counter < 3) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(Round.SHOWDOWN, testGame.getRound());

    }

    @Test
    void userRaises_success() {
        Long id = getIdOfUserOnTurn();
        User user = getOnTurnUser();

        gameService.userCallsForRaising(testGame, id);
        gameService.userRaises(testGame, id, raiseamountpossible);

        assertEquals(4795, user.getMoney());
        assertEquals(user.getUsername(), testGame.getUserThatRaisedLast().getUsername());
        assertEquals(505, testGame.getPot().getTotal());

        id = getIdOfUserOnTurn();
        user = getOnTurnUser();

        gameService.userCallsForRaising(testGame, id);
        gameService.userRaises(testGame, id, raiseamountpossible);

        assertEquals(4790, user.getMoney());
        assertEquals(user.getUsername(), testGame.getUserThatRaisedLast().getUsername());
        assertEquals(715, testGame.getPot().getTotal());

    }

    @Test
    void userRaises_nextUserComesInTurn() {
        User onTurn = getOnTurnUser();
        int indexOfonTurn = testGame.getActiveUsers().indexOf(onTurn);
        int indexOfUserAfteronTurn = Math.abs((indexOfonTurn - 1 + testGame.getAllUsers().size()) % (testGame.getAllUsers().size()));
        User userAfteronTurn = testGame.getActiveUsers().get(indexOfUserAfteronTurn);

        gameService.userCallsForRaising(testGame, onTurn.getId());
        gameService.userRaises(testGame, onTurn.getId(), 300);
        assertEquals(userAfteronTurn.getUsername(), testGame.getOnTurn().getUsername());
    }

    @Test
    void userRaises_success_playerGoesAllIn() {
        Long id = getIdOfUserOnTurn();
        User user = getOnTurnUser();

        gameService.userCallsForRaising(testGame, id);
        gameService.userRaises(testGame, id, user.getMoney());
        assertEquals(0, user.getMoney());
        assertEquals(5300, testGame.getPot().getTotal());
        assertEquals(user.getUsername(), testGame.getUserThatRaisedLast().getUsername());
    }

    @Test
    void userRaises_toohighamount() {
        Long id = getIdOfUserOnTurn();
        gameService.userCallsForRaising(testGame, id);
        assertThrows(ResponseStatusException.class, () -> gameService.userRaises(testGame, id, raiseamounttoomuch));
        assertNotEquals(id, testGame.getUserThatRaisedLast().getId());
        assertEquals(500, testGame.getPot().getTotal());

    }

    @Test
    void userRaises_negativeAmount_causesConflict() {
        Long id = getIdOfUserOnTurn();
        gameService.userCallsForRaising(testGame, id);
        assertThrows(ResponseStatusException.class, () -> gameService.userRaises(testGame, id, -2));
        assertNotEquals(id, testGame.getUserThatRaisedLast().getId());
        assertEquals(500, testGame.getPot().getTotal());
    }

    @Test
    void userRaises_butWasNotFound() {
        assertThrows(ResponseStatusException.class, () -> gameService.userCallsForRaising(testGame, 6L));
        assertThrows(ResponseStatusException.class, () -> gameService.userRaises(testGame, 6L, raiseamountpossible));
    }

    @Test
    void userRaises_butwasplayerthatraisedlast_isNotOnTurn() {
        User user = getOnTurnUser();
        String username = user.getUsername();
        Long id = getIdOfUserOnTurn();

        gameService.userCallsForRaising(testGame, id);
        gameService.userRaises(testGame, id, raiseamountpossible);

        assertEquals(4795, user.getMoney());
        assertEquals(user.getUsername(), testGame.getUserThatRaisedLast().getUsername());
        assertEquals(505, testGame.getPot().getTotal());
        assertThrows(ResponseStatusException.class, () -> gameService.userCallsForRaising(testGame, id));
        assertThrows(ResponseStatusException.class, () -> gameService.userRaises(testGame, id, raiseamountpossible));

        for (User aUser : testGame.getActiveUsers()) {
            if (aUser.getUsername().equals(username)) {
                assertEquals(4795, aUser.getMoney());
                assertEquals(aUser.getUsername(), testGame.getUserThatRaisedLast().getUsername());
            }
        }

        assertEquals(505, testGame.getPot().getTotal());

    }

    @Test
    void userRaises_butDoesNotEvenHaveEnoughMoneyToCall() {
        User winner;
        int counter = 0;
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        counter = 0;
        //Big Blind should be the winner -> Small Blind folds
        gameService.userFolds(testGame, getIdOfUserOnTurn());
        //Big Blind calls
        gameService.userCalls(testGame, getIdOfUserOnTurn());
        //The rest of the players fold
        while (counter < 3) {
            gameService.userFolds(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(Round.PREFLOP, testGame.getRound());
        //Remember: The old Big Blind won the previous round. This old Big Blind is the new Small Blind
        counter = 0;
        //In the PreFlop Round everyone should call
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        //Now we get some fun: The winner from the last round, the new Small Blind, should raise with an amount such that the next player has to go All-In
        gameService.userRaises(testGame, getIdOfUserOnTurn(), 4599);

        counter = 0;
        while (counter < 4) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        gameService.userRaises(testGame, getIdOfUserOnTurn(), 2);
        long idOfUserOnTurn = getIdOfUserOnTurn();

        assertThrows(ResponseStatusException.class, () -> gameService.userCallsForRaising(testGame, idOfUserOnTurn));

    }

    @Test
    void userCalls_success() {
        User myUser = getOnTurnUser();
        String username = myUser.getUsername();
        Long id = getIdOfUserOnTurn();

        gameService.userCallsForRaising(testGame, id);
        gameService.userRaises(testGame, id, raiseamountpossible);

        assertEquals(4795, myUser.getMoney());
        assertEquals(myUser.getUsername(), testGame.getUserThatRaisedLast().getUsername());
        assertEquals(505, testGame.getPot().getTotal());

        myUser = getOnTurnUser();
        String username2 = myUser.getUsername();
        Long id2 = getIdOfUserOnTurn();

        gameService.userCalls(testGame, id2);
        assertEquals(4795, myUser.getMoney());
        assertEquals(id, testGame.getUserThatRaisedLast().getId());
        assertEquals(710, testGame.getPot().getTotal());
    }

    @Test
    void userCalls_nextUserComesInTurn() {
        User onTurn = getOnTurnUser();
        int indexOfonTurn = testGame.getActiveUsers().indexOf(onTurn);
        int indexOfUserAfteronTurn = Math.abs((indexOfonTurn - 1 + testGame.getAllUsers().size()) % (testGame.getAllUsers().size()));
        User userAfteronTurn = testGame.getActiveUsers().get(indexOfUserAfteronTurn);

        gameService.userCalls(testGame, onTurn.getId());
        assertEquals(userAfteronTurn.getUsername(), testGame.getOnTurn().getUsername());
    }

    @Test
    void userCalls_firstUserOnTurn_nooneraised_onlysmallandbigblindputtheirinput_success() {
        User onTurn = getOnTurnUser();
        Long id2 = getIdOfUserOnTurn();
        gameService.userCalls(testGame, id2);
        assertEquals(4800, onTurn.getMoney());
        assertEquals(500, testGame.getPot().getTotal());
    }

    @Test
    void userCalls_HasToGoAllIn() {
        User winner;
        int counter = 0;
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        counter = 0;
        //Big Blind should be the winner -> Small Blind folds
        gameService.userFolds(testGame, getIdOfUserOnTurn());
        //Big Blind calls
        gameService.userCalls(testGame, getIdOfUserOnTurn());
        //The rest of the players fold
        while (counter < 3) {
            gameService.userFolds(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(Round.PREFLOP, testGame.getRound());
        //Remember: The old Big Blind won the previous round. This old Big Blind is the new Small Blind
        counter = 0;
        //In the PreFlop Round everyone should call
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        //Now we get some fun: The winner from the last round, the new Small Blind, should raise with an amount such that the next player has to go All-In
        gameService.userRaises(testGame, getIdOfUserOnTurn(), 5400);
        User temporaryOnTurn = getOnTurnUser();
        assertEquals(4600, temporaryOnTurn.getMoney());
        gameService.userCalls(testGame, getIdOfUserOnTurn());
        assertEquals(0, temporaryOnTurn.getMoney());

    }

    @Test
    void userChecks_success() {
        //for checking, we are going to be in the FLOP round
        int counter = 0;
        while (counter < 5) {
            Long id = getIdOfUserOnTurn();
            gameService.userCalls(testGame, id);
            counter++;
        }
        //now we should be in the FLOP round -> everyone will check now (The Test should test if this works)
        assertEquals(Round.FLOP, testGame.getRound());

        counter = 0;
        while (counter < 5) {
            Long id = getIdOfUserOnTurn();
            gameService.userChecks(testGame, id);
            counter++;
        }
        for (User user : testGame.getActiveUsers()) {
            assertEquals(200, testGame.getPot().getUserContributionOfAUser(user));
            assertEquals(4800, user.getMoney());
        }

        assertEquals(1000, testGame.getPot().getTotal());
    }

    @Test
    void userChecks_nextUserComesInTurn() {
        int counter = 0;
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }

        User onTurn = getOnTurnUser();
        int indexOfonTurn = testGame.getActiveUsers().indexOf(onTurn);
        int indexOfUserAfteronTurn = Math.abs((indexOfonTurn - 1 + testGame.getAllUsers().size()) % (testGame.getAllUsers().size()));
        User userAfteronTurn = testGame.getActiveUsers().get(indexOfUserAfteronTurn);

        gameService.userChecks(testGame, onTurn.getId());
        assertEquals(userAfteronTurn.getUsername(), testGame.getOnTurn().getUsername());
    }

    @Test
    void userChecks_youShallNotCheckBecauseWeAreInPreFlop() {
        Long id2 = getIdOfUserOnTurn();
        User theUser = getOnTurnUser();

        assertThrows(ResponseStatusException.class, () -> gameService.userChecks(testGame, id2));
        assertEquals(5000, theUser.getMoney());
        assertEquals(theUser.getUsername(), testGame.getOnTurn().getUsername());
        assertEquals(300, testGame.getPot().getTotal());

    }

    @Test
    void userChecks_butIsNotOnTurn() {
        int counter = 0;
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        Long id = getIdOfUserOnTurn();
        gameService.userChecks(testGame, getIdOfUserOnTurn());
        assertThrows(ResponseStatusException.class, () -> gameService.userChecks(testGame, id));

    }

    @Test
    void allUsersExceptOneFold_oneClearWinner_testingWithPreFlop() {
        User bigblind = getBigBlind();

        int counter = 0;
        while (counter < 4) {
            gameService.userFolds(testGame, getIdOfUserOnTurn());
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
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        //Now, 200 * 5 = 1000 are in the pot
        //at the beginning of each new round (not being Preflop), the small blind starts

        assertEquals(Round.FLOP, testGame.getRound());
        assertEquals(1000, testGame.getPot().getTotal());

        counter = 0;
        while (counter < 5) {
            gameService.userChecks(testGame, getIdOfUserOnTurn());
            counter++;
        }
        counter = 0;

        assertEquals(Round.TURNCARD, testGame.getRound());
        assertEquals(1000, testGame.getPot().getTotal());

        gameService.userChecks(testGame, getIdOfUserOnTurn());
        while (counter < 4) {
            gameService.userFolds(testGame, getIdOfUserOnTurn());
            counter++;
        }

        assertEquals(Round.PREFLOP, testGame.getRound());
        assertEquals(300, testGame.getPot().getTotal());
        assertEquals(5800, smallBlind.getMoney());
    }

    @Test
    void userWhoRaisedLastIsReached_NextRoundShouldStart() {
        int counter = 0;
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(Round.FLOP, testGame.getRound());
        gameService.userRaises(testGame, getIdOfUserOnTurn(), 500);

        counter = 0;
        while (counter < 4) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(Round.TURNCARD, testGame.getRound());
    }

    @Test
    void userWhoStartedTheRoundIsReachedAgain_EveryoneChecked_NextRoundShouldStart() {
        int counter = 0;
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(Round.FLOP, testGame.getRound());
        counter = 0;
        while (counter < 5) {
            gameService.userChecks(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(Round.TURNCARD, testGame.getRound());
    }

    @Test
    void flopRoundIsReached_3CardsAreRevealed() {
        int counter = 0;
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(3, testGame.getRiver().getCards().size());
        assertEquals(Round.FLOP, testGame.getRound());


    }

    @Test
    void riverCardRoundisReached_weHave5CardsInTheRiver() {
        int counter = 0;
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(3, testGame.getRiver().getCards().size());
        assertEquals(Round.FLOP, testGame.getRound());
        counter = 0;
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(4, testGame.getRiver().getCards().size());
        assertEquals(Round.TURNCARD, testGame.getRound());
        counter = 0;
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(5, testGame.getRiver().getCards().size());
        assertEquals(Round.RIVERCARD, testGame.getRound());
    }

    @Test
    void testingNewDeck() {
        Deck firstdeck = testGame.getDeck();
        int counter = 0;
        while (counter < 4) {
            gameService.userFolds(testGame, getIdOfUserOnTurn());
            counter++;
        }
        Deck seconddeck = testGame.getDeck();
        assertNotSame(firstdeck, seconddeck);
    }

    @Test
    void specialCase_BigBlindCanRaiseEvenIfEveryOneCalled() {
        User bigblind = getBigBlind();
        int counter = 0;
        while (counter < 4) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(bigblind.getId(), getIdOfUserOnTurn());
        assertEquals(Round.PREFLOP, testGame.getRound());
        gameService.userRaises(testGame, getIdOfUserOnTurn(), 1300);
        counter = 0;
        while (counter < 4) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(7500, testGame.getPot().getTotal());
        assertEquals(Round.FLOP, testGame.getRound());
        assertEquals(getSmallBlind().getId(), getIdOfUserOnTurn());


    }

    @Test
    void specialCase_BigBlindCanFoldEvenIfEveryOneCalled() {
        User bigblind = getBigBlind();
        int counter = 0;
        while (counter < 4) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(bigblind.getId(), getIdOfUserOnTurn());
        assertEquals(Round.PREFLOP, testGame.getRound());
        gameService.userFolds(testGame, getIdOfUserOnTurn());
        assertEquals(Round.FLOP, testGame.getRound());
        assertEquals(1000, testGame.getPot().getTotal());
        assertEquals(getSmallBlind().getId(), getIdOfUserOnTurn());


    }

    @Test
    void specialCase_BigBlindCanCallEvenIfEveryOneCalled() {
        User bigblind = getBigBlind();
        int counter = 0;
        while (counter < 4) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(bigblind.getId(), getIdOfUserOnTurn());
        assertEquals(Round.PREFLOP, testGame.getRound());
        gameService.userCalls(testGame, getIdOfUserOnTurn());
        assertEquals(Round.FLOP, testGame.getRound());
        assertEquals(1000, testGame.getPot().getTotal());
        assertEquals(getSmallBlind().getId(), getIdOfUserOnTurn());
    }

    @Test
    void callingIsTheSameAsCheckingWhenNobodyRaised_inFlopRound() {
        int counter = 0;
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        assertEquals(Round.FLOP, testGame.getRound());
        assertEquals(1000, testGame.getPot().getTotal());
        assertEquals(getSmallBlind().getId(), getIdOfUserOnTurn());
        gameService.userCalls(testGame, getIdOfUserOnTurn());
        assertEquals(Round.FLOP, testGame.getRound());
        assertEquals(1000, testGame.getPot().getTotal());
        assertEquals(getBigBlind().getId(), getIdOfUserOnTurn());
    }


    @Test
    void userNeedsToCallBeforeRaisingButHasMinusMoney_specialCase_shouldThrowException() {
        int counter = 0;
        while (counter < 5) {
            gameService.userCalls(testGame, getIdOfUserOnTurn());
            counter++;
        }
        gameService.userRaises(testGame, getIdOfUserOnTurn(), 400);
        for (User user : testGame.getActiveUsers()) {
            if (user.getId().equals(getIdOfUserOnTurn())) {
                user.setMoney(-1);
                break;
            }
        }
        Long idOfUserOnTurn = getIdOfUserOnTurn();

        assertThrows(ResponseStatusException.class, () -> gameService.userCallsForRaising(testGame, idOfUserOnTurn));
    }

    @Test
    void logCreatedForPlayerActions() {
        var firstUser = getOnTurnUser();
        gameService.userCalls(testGame, getIdOfUserOnTurn());
        var secondUser = getOnTurnUser();
        gameService.userRaises(testGame, getIdOfUserOnTurn(), 100);
        var thirdUser = getOnTurnUser();
        gameService.userFolds(testGame, getIdOfUserOnTurn());

        var expectedType = MessageType.LOG;
        var expectedName = testGame.getName();

        var p = testGame.getProtocol();
        var actualType = p.get(0).getMessageType();
        var actualName = p.get(0).getName();
        assertEquals(expectedName, actualName);
        assertEquals(expectedType, actualType);

        actualType = p.get(1).getMessageType();
        actualName = p.get(1).getName();
        assertEquals(expectedName, actualName);
        assertEquals(expectedType, actualType);

        actualType = p.get(2).getMessageType();
        actualName = p.get(2).getName();
        assertEquals(expectedName, actualName);
        assertEquals(expectedType, actualType);
    }
}







