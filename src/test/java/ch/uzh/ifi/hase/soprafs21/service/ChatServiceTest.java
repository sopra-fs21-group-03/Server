package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.Blind;
import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
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

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ChatServiceTest {
    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private ChatService chatService;

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

    @Test
    void addChatMessage_success() {
        chatService.addChatMessage(testGame.getId(), testUser.getId(), "Hello there :D");
        chatService.addChatMessage(testGame.getId(), testUser2.getId(), "Sali zeme!");
        chatService.addChatMessage(testGame.getId(), testUser3.getId(), "Sali!");
        chatService.addChatMessage(testGame.getId(), testUser4.getId(), "Hoi!");
        chatService.addChatMessage(testGame.getId(), testUser5.getId(), "Ciao!");
        assertEquals(6, chatService.getProtocol(testGame.getId()).size());
        assertEquals("New Gameround starts", testGame.getProtocol().get(0).getMessage());
        assertEquals("User testUsername1 says: Hello there :D", testGame.getProtocol().get(1).getMessage());
        assertEquals("User testUsername2 says: Sali zeme!", testGame.getProtocol().get(2).getMessage());
        assertEquals("User testUsername3 says: Sali!", testGame.getProtocol().get(3).getMessage());
        assertEquals("User testUsername4 says: Hoi!", testGame.getProtocol().get(4).getMessage());
        assertEquals("User testUsername5 says: Ciao!", testGame.getProtocol().get(5).getMessage());

    }

    @Test
    void addChatMessage_gameEntityNotFound() {
        assertThrows(ResponseStatusException.class, () -> chatService.addChatMessage(3L, testUser.getId(), "Hello there :D"));
    }

    @Test
    void addChatMessage_userWasNotFound() {
        assertThrows(ResponseStatusException.class, () -> chatService.addChatMessage(testGame.getId(), testUser.getId() + 100, "Hello there :D"));

    }


}