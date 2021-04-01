package ch.uzh.ifi.hase.soprafs21.game;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PotTest {

    @Test
    void addMoney() {
        Pot pot = new Pot();
        User user = new User();
        pot.addUser(user);
        int money = 100;

        pot.addMoney(user, money);
        int expected = money;
        assertEquals(expected, pot.getUserContribution().get(user));

        pot.addMoney(user, money);
        expected += money;
        assertEquals(expected, pot.getUserContribution().get(user));
    }

    @Test
    void addUser() {
        Pot pot = new Pot();
        User user = new User();
        User user2 = new User();

        pot.addUser(user);
        assertTrue(pot.getUserContribution().containsKey(user));
        assertEquals(0, pot.getUserContribution().get(user));

        pot.addUser(user2);
        assertTrue(pot.getUserContribution().containsKey(user2));
        assertEquals(0, pot.getUserContribution().get(user2));
        assertTrue(pot.getUserContribution().containsKey(user));
        assertEquals(0, pot.getUserContribution().get(user));

    }

    @Test
    void removeUser() {
        Pot pot = new Pot();
        User user = new User();
        pot.addUser(user);

        pot.removeUser(user);
        assertTrue(pot.getUserContribution().isEmpty());

        User user2 = new User();
        pot.addUser(user);
        pot.addUser(user2);
        pot.removeUser(user2);
        assertTrue(pot.getUserContribution().containsKey(user));
        assertFalse(pot.getUserContribution().containsKey(user2));
    }

    @Test
    void distribute() {
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        Pot pot = new Pot();

    }
}