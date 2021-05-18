package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs21.constant.Round;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LobbyServiceTest {
    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GameService gameService;

    @InjectMocks
    private LobbyService lobbyService;


    private User testUser;
    private User testUser2;
    private User testUser3;
    private User testUser4;
    private User testUser5;
    private User alloneTestUser;

    private GameEntity testGameFull;
    private GameEntity testGameNotFull;

    private ArrayList<User> testAllUsersFull;
    private List<GameEntity> testListForGameEntities;


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
        testUser.setGamestatus(GameStatus.NOTREADY);

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setPassword("testName");
        testUser2.setUsername("testUsername2");
        testUser2.setToken("2");
        testUser2.setMoney(10);
        testUser2.setGamestatus(GameStatus.NOTREADY);

        testUser3 = new User();
        testUser3.setId(3L);
        testUser3.setPassword("testName");
        testUser3.setUsername("testUsername3");
        testUser3.setToken("3");
        testUser3.setMoney(10);
        testUser3.setGamestatus(GameStatus.NOTREADY);

        testUser4 = new User();
        testUser4.setId(4L);
        testUser4.setPassword("testName");
        testUser4.setUsername("testUsername4");
        testUser4.setToken("4");
        testUser4.setMoney(10);
        testUser4.setGamestatus(GameStatus.NOTREADY);

        testUser5 = new User();
        testUser5.setId(5L);
        testUser5.setPassword("testName");
        testUser5.setUsername("testUsername5");
        testUser5.setToken("5");
        testUser5.setMoney(10);
        testUser5.setGamestatus(GameStatus.NOTREADY);

        alloneTestUser = new User();
        alloneTestUser.setId(6L);
        alloneTestUser.setPassword("testName");
        alloneTestUser.setUsername("alone1");
        alloneTestUser.setToken("alone");
        alloneTestUser.setMoney(10);
        alloneTestUser.setGamestatus(GameStatus.NOTREADY);



        testGameFull = new GameEntity(1L);
        testGameNotFull = new GameEntity(2L);

        testAllUsersFull = new ArrayList<User>();
        testAllUsersFull.add(testUser);
        testAllUsersFull.add(testUser2);
        testAllUsersFull.add(testUser3);
        testAllUsersFull.add(testUser4);
        testAllUsersFull.add(testUser5);

        testGameFull.setGameName("FullGame");
        testGameNotFull.setGameName("NotFull");

        testListForGameEntities = new ArrayList<>();
        testListForGameEntities.add(testGameFull);
        testListForGameEntities.add(testGameNotFull);


        // when -> any object is being save in the gameRepository -> return the dummy testGame

        Mockito.when(gameRepository.findById(testGameFull.getId())).thenReturn(Optional.ofNullable(testGameFull));
        Mockito.when(gameRepository.findById(testGameNotFull.getId())).thenReturn(Optional.ofNullable(testGameNotFull));
        Mockito.when(gameRepository.findAll()).thenReturn(testListForGameEntities);
        Mockito.when(userRepository.findByToken(testUser.getToken())).thenReturn(testUser);

    }

    @Test
    void userSetsUnready_success(){
        // Hardcode ready status
        testUser.setGamestatus(GameStatus.READY);

        // Change status with method call
        lobbyService.setUserToUnready(testUser);

        assertEquals(GameStatus.NOTREADY, testUser.getGamestatus());
    }

    @Test
    void userLeavesLobby_success(){
        // add test user to game
        lobbyService.addUserToGame(testUser, testGameFull);

        // test users leaves game
        lobbyService.leaveLobby(testUser, testGameFull);

        assertFalse(testGameFull.getAllUsers().contains(testUser));

    }

    @Test
    void userLeavesLobby_notFound(){
        assertThrows(ResponseStatusException.class, () ->  lobbyService.leaveLobby(testUser, testGameFull));
    }

    @Test
    void userLeavesLobby_conflict(){
        // game has already started
        // Mock that every user joins and is ready
        for (User user: testAllUsersFull){
            lobbyService.addUserToGame(user, testGameFull);
            lobbyService.setUserToReady(user);
        }
        testGameFull.setup();
        // Now an exception should be thrown, because you can only leave via the leave endpoint in the gamecontroller
        assertThrows(ResponseStatusException.class, () -> lobbyService.leaveLobby(testUser, testGameFull));
    }

    @Test
    void getAllGames_success(){
        var list = lobbyService.getAllGames();
        assertEquals(2, list.size());
        assertEquals("FullGame", list.get(0).getGameName());
        assertEquals("NotFull", list.get(1).getGameName());
    }

    @Test
    void findGameEntity_success(){
        var entity = lobbyService.findGameEntity(1L);
        assertEquals("FullGame", entity.getGameName());
        assertEquals(1L, entity.getId());
    }

    @Test
    void findGameEntity_notSuccessful_entityNotFound(){
        assertThrows(ResponseStatusException.class, () -> lobbyService.findGameEntity(6L));
    }

    @Test
    void checkIfUserExists_ByToken_userNotFound(){
        assertThrows(ResponseStatusException.class, () -> lobbyService.checkIfUserExistsByToken("novalidtoken"));

    }

    @Test
    void addUserToGame_success_onlyOneUserGetsIntoGameSession(){
        lobbyService.addUserToGame(testUser, testGameFull);
        var entity = lobbyService.findGameEntity(testGameFull.getId());
        assertEquals(1, entity.getAllUsers().size());
        assertEquals(1, entity. getActiveUsers().size());
        assertTrue(testGameFull.getAllUsers().contains(testUser) && testGameFull.getActiveUsers().contains(testUser));
        assertEquals(GameStatus.NOTREADY, testGameFull.getAllUsers().get(0).getGamestatus());
        assertFalse(testGameFull.getGameCanStart());
        assertEquals(Round.NOTSTARTED,testGameFull.getRound());
    }

    @Test
    void addUserToGame_success_gameSessionIsFull_butNobodyIsReady(){
        for (User user: testAllUsersFull){
            lobbyService.addUserToGame(user, testGameFull);
        }

        var entity = lobbyService.findGameEntity(testGameFull.getId());
        assertEquals(5, entity.getAllUsers().size());
        assertEquals(5, entity. getActiveUsers().size());

        for (User user: testAllUsersFull){
            assertTrue(testGameFull.getAllUsers().contains(user) && testGameFull.getActiveUsers().contains(user));
            int index = testGameFull.getAllUsers().indexOf(user);
            assertEquals(GameStatus.NOTREADY, testGameFull.getAllUsers().get(index).getGamestatus());
        }
        assertFalse(testGameFull.getGameCanStart());
        assertEquals(Round.NOTSTARTED,testGameFull.getRound());
    }

    @Test
    void addUserToGame_success_gameSessionIsNowFull_everyOneIsReady_thereforeGameCanStart(){
        for (User user: testAllUsersFull){
            lobbyService.addUserToGame(user, testGameFull);
            lobbyService.setUserToReady(user);
        }

        var entity = lobbyService.findGameEntity(testGameFull.getId());
        assertEquals(5, entity.getAllUsers().size());
        assertEquals(5, entity. getActiveUsers().size());

        for (User user: testAllUsersFull){
            assertTrue(testGameFull.getAllUsers().contains(user) && testGameFull.getActiveUsers().contains(user));
            int index = testGameFull.getAllUsers().indexOf(user);
            assertEquals(GameStatus.READY, testGameFull.getAllUsers().get(index).getGamestatus());
        }
        assertTrue(testGameFull.getGameCanStart());
        lobbyService.setUpGame(entity);
        assertEquals(Round.PREFLOP,testGameFull.getRound());
    }

    @Test
    void addUserToGame_fail_gameSessionIsAlreadyFull(){
        for (User user: testAllUsersFull){
            lobbyService.addUserToGame(user, testGameFull);
        }
        var entity = lobbyService.findGameEntity(testGameFull.getId());
        assertThrows(ResponseStatusException.class, () -> lobbyService.addUserToGame(alloneTestUser, entity));

    }

    @Test
    void addUserToGame_fail_gameSessionIsAlreadyStarted(){
        for (User user: testAllUsersFull){
            lobbyService.addUserToGame(user, testGameFull);
            lobbyService.setUserToReady(user);
        }
        var entity = lobbyService.findGameEntity(testGameFull.getId());
        lobbyService.setUpGame(entity);
        assertThrows(ResponseStatusException.class, () -> lobbyService.addUserToGame(alloneTestUser, entity));

    }

    @Test
    void getUserInSpecificGameSessionInAllUsers_success(){
        lobbyService.addUserToGame(testUser, testGameFull);
        var entity = lobbyService.findGameEntity(testGameFull.getId());
        var user = lobbyService.getUserInSpecificGameSessionInAllUsers(testUser.getId(), entity);
        assertEquals(testUser.getId(), user.getId());
        assertEquals(testUser.getUsername(), user.getUsername());

    }

    @Test
    void getUserInSpecificGameSessionInAllUsers_fail_userNotFound(){
        lobbyService.addUserToGame(testUser, testGameFull);
        var entity = lobbyService.findGameEntity(testGameFull.getId());
        assertThrows(ResponseStatusException.class, () ->lobbyService.getUserInSpecificGameSessionInAllUsers(testUser2.getId(), entity));
    }

    @Test
    void findByToken_success(){
        var user = lobbyService.getUserByTokenInUserRepository(testUser.getToken());
        assertEquals(user.getUsername(), testUser.getUsername());
        assertEquals(user.getId(), testUser.getId());
    }

    @Test
    void getSpecificLobbyData_success(){
        lobbyService.addUserToGame(testUser3, testGameNotFull);
        var entity = lobbyService.getSpecificLobbyData(testGameNotFull.getId());
        assertEquals(testGameNotFull.getId(), entity.getId());
        assertEquals(testGameNotFull.getName(), entity.getName());
        assertEquals(1, testGameNotFull.getLobbyplayers().size());
        assertEquals(GameStatus.NOTREADY, testGameNotFull.getLobbyplayers().get(0).getReadyStatus());
        assertEquals(testUser3.getUsername(), testGameNotFull.getLobbyplayers().get(0).getUsername());
    }

    @Test
    void checkIfUserIsInLobbySession_success_userIsInAllUsers(){
        lobbyService.addUserToGame(testUser2, testGameNotFull);
        assertDoesNotThrow(() ->lobbyService.checkIfUserIsInGameSession("2", testGameNotFull));
    }

    @Test
    void checkIfUserIsInLobbySession_success_userInSpectators(){
        testGameNotFull.getSpectators().add(testUser2);
        assertDoesNotThrow(() ->lobbyService.checkIfUserIsInGameSession("2", testGameNotFull));
    }

    @Test
    void checkIfUserIsInLobbySession_fail_notFound(){
        assertThrows(ResponseStatusException.class, () ->lobbyService.checkIfUserIsInGameSession("4", testGameFull));
    }

    @Test
    void checkIfUserIsAlreadyInAnotherLobby_success_heIsNot(){
        assertDoesNotThrow(()-> lobbyService.checkIfUserIsAlreadyInAnOtherLobby(testUser4.getToken(), 1L, testListForGameEntities));
    }

    @Test
    void checkIfUserIsAlreadyInAnotherLobby_fail_heIsInAllUsersInAnOtherLobby(){
        lobbyService.addUserToGame(testUser, testGameFull);
        assertThrows(ResponseStatusException.class,()-> lobbyService.checkIfUserIsAlreadyInAnOtherLobby("1", 2L, testListForGameEntities));
    }

    @Test
    void checkIfUserIsAlreadyInAnotherLobby_fail_heIsInSpectatorInAnOtherLobby(){
        testGameNotFull.getSpectators().add(testUser2);
        assertThrows(ResponseStatusException.class,()-> lobbyService.checkIfUserIsAlreadyInAnOtherLobby("2", 1L, testListForGameEntities));
    }



}