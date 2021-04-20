package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.Blind;
import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameEntityTest {

    private GameEntity testGame;
    private User testUser;
    private User testUser2;
    private User testUser3;
    private User testUser4;
    private User testUser5;

    private User getUser_providedUsername_inactiveUsers(String username) {
        User theUser = null;
        for (User user : testGame.getActiveUsers()) {
            if (user.getUsername().equals(username)) {
                theUser = user;
            }
        }
        return theUser;
    }

    private User getUser_providedUsername_inallUsers(String username) {
        User theUser = null;
        for (User user : testGame.getAllUsers()) {
            if (user.getUsername().equals(username)) {
                theUser = user;
            }
        }
        return theUser;
    }

    private User getSmallBlind_inallUsers() {
        User small = null;
        for (User user : testGame.getAllUsers()) {
            if (user.getBlind() == Blind.SMALL) {
                small = user;
            }
        }
        return small;
    }

    private User getSmallBlind_inactiveUsers() {
        User small = null;
        for (User user : testGame.getActiveUsers()) {
            if (user.getBlind() == Blind.SMALL) {
                small = user;
            }
        }
        return small;
    }

    private User getBigBlind_inallUsers() {
        User big = null;
        for (User user : testGame.getAllUsers()) {
            if (user.getBlind() == Blind.BIG) {
                big = user;
            }
        }
        return big;
    }

    private void removeAllPlayersFromActiveExceptOne() {
        int counter = 0;
        while (counter < 4) {
            testGame.getActiveUsers().remove(0);
            counter++;
        }
    }

    private User getBigBlind_inactiveUsers() {
        User big = null;
        for (User user : testGame.getActiveUsers()) {
            if (user.getBlind() == Blind.BIG) {
                big = user;
            }
        }
        return big;
    }

    @BeforeEach
    void setup() {
        testGame = new GameEntity();
        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername1");
        testUser.setToken("1");

        testUser.setGamestatus(GameStatus.READY);

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setPassword("testName");
        testUser2.setUsername("testUsername2");
        testUser2.setToken("2");

        testUser2.setGamestatus(GameStatus.READY);

        testUser3 = new User();
        testUser3.setId(3L);
        testUser3.setPassword("testName");
        testUser3.setUsername("testUsername3");
        testUser3.setToken("3");

        testUser3.setGamestatus(GameStatus.READY);

        testUser4 = new User();
        testUser4.setId(4L);
        testUser4.setPassword("testName");
        testUser4.setUsername("testUsername4");
        testUser4.setToken("4");

        testUser4.setGamestatus(GameStatus.READY);

        testUser5 = new User();
        testUser5.setId(5L);
        testUser5.setPassword("testName");
        testUser5.setUsername("testUsername5");
        testUser5.setToken("5");

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

    }
    
    @Test
    void blindRoleWillBeReassigned_success() {
        //After a gameround, each Blind role will be reassigned to the left player of the last Blind
        User big = getBigBlind_inallUsers();
        User small = getSmallBlind_inallUsers();
        String userOnTurn = testGame.getOnTurn().getUsername();
        removeAllPlayersFromActiveExceptOne();
        testGame.setNextUserOrNextRoundOrSomeoneHasAlreadyWon("NotImportant");
        assertEquals(big.getUsername(), getSmallBlind_inallUsers().getUsername());
        assertEquals(Blind.SMALL, big.getBlind());
        assertEquals(Blind.NEUTRAL, small.getBlind());
        assertEquals(Blind.BIG, getUser_providedUsername_inallUsers(userOnTurn).getBlind());

    }

    @Test
    void getUsernameOfPotentialNextUserInTurn() {
    }

    @Test
    void setNextUserOrNextRoundOrSomeoneHasAlreadyWon() {
    }

    @Test
    void setupTest() {
    }
}