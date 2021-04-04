package ch.uzh.ifi.hase.soprafs21.game;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.helper.UserDraw;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
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
    void distributeOneWinner() {
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        Pot pot = new Pot();
        pot.addUser(user1);
        pot.addUser(user2);
        pot.addUser(user3);

        pot.addMoney(user1,100);
        pot.addMoney(user2, 100);
        pot.addMoney(user3, 100);
        ArrayList<UserDraw> ranking = new ArrayList<>();
        UserDraw userD1 = new UserDraw();
        userD1.addUser(user1, pot.getUserContribution().get(user1));
        UserDraw userD2 = new UserDraw();
        userD2.addUser(user2, pot.getUserContribution().get(user2));
        UserDraw userD3 = new UserDraw();
        userD3.addUser(user3, pot.getUserContribution().get(user3));

        ranking.add(userD2);
        ranking.add(userD1);
        ranking.add(userD3);

        pot.distribute(ranking);
        assertEquals(0, user1.getMoney());
        assertEquals(300, user2.getMoney());
        assertEquals(0, user3.getMoney());
    }

    @Test
    void distributePartial() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        User user3 = new User();
        user3.setId(3L);
        User user4 = new User();
        user4.setId(4L);
        Pot pot = new Pot();
        pot.addUser(user1);
        pot.addUser(user2);
        pot.addUser(user3);
        pot.addUser(user4);

        pot.addMoney(user1,100);
        pot.addMoney(user2, 50);
        pot.addMoney(user3, 150);
        pot.addMoney(user4, 150);
        ArrayList<UserDraw> ranking = new ArrayList<>();
        UserDraw userD1 = new UserDraw();
        userD1.addUser(user1, pot.getUserContribution().get(user1));
        UserDraw userD2 = new UserDraw();
        userD2.addUser(user2, pot.getUserContribution().get(user2));
        UserDraw userD3 = new UserDraw();
        userD3.addUser(user3, pot.getUserContribution().get(user3));
        UserDraw userD4 = new UserDraw();
        userD4.addUser(user4, pot.getUserContribution().get(user4));

        ranking.add(userD2);
        ranking.add(userD1);
        ranking.add(userD3);
        ranking.add(userD4);

        pot.distribute(ranking);
        assertEquals(150, user1.getMoney());
        assertEquals(200, user2.getMoney());
        assertEquals(100, user3.getMoney());
        assertEquals(0, user4.getMoney());
    }

    @Test
    void distributeDraw() {
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        Pot pot = new Pot();
        pot.addUser(user1);
        pot.addUser(user2);
        pot.addUser(user3);

        pot.addMoney(user1,100);
        pot.addMoney(user2, 100);
        pot.addMoney(user3, 100);
        ArrayList<UserDraw> ranking = new ArrayList<>();
        UserDraw userD1 = new UserDraw();
        userD1.addUser(user1, pot.getUserContribution().get(user1));
        userD1.addUser(user2, pot.getUserContribution().get(user2));
        UserDraw userD3 = new UserDraw();
        userD3.addUser(user3, pot.getUserContribution().get(user3));

        ranking.add(userD1);
        ranking.add(userD3);

        pot.distribute(ranking);
        assertEquals(150, user1.getMoney());
        assertEquals(150, user2.getMoney());
        assertEquals(0, user3.getMoney());

    }

    @Test
    void distributeDrawPartial() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        User user3 = new User();
        user3.setId(3L);
        User user4 = new User();
        user4.setId(4L);
        Pot pot = new Pot();
        pot.addUser(user1);
        pot.addUser(user2);
        pot.addUser(user3);
        pot.addUser(user4);

        pot.addMoney(user1,50);
        pot.addMoney(user2, 100);
        pot.addMoney(user3, 200);
        pot.addMoney(user4, 200);
        ArrayList<UserDraw> ranking = new ArrayList<>();
        UserDraw userD1 = new UserDraw();
        userD1.addUser(user1, pot.getUserContribution().get(user1));
        UserDraw userD2 = new UserDraw();
        userD2.addUser(user2, pot.getUserContribution().get(user2));
        userD2.addUser(user4, pot.getUserContribution().get(user4));
        UserDraw userD3 = new UserDraw();
        userD3.addUser(user3, pot.getUserContribution().get(user3));

        ranking.add(userD1);
        ranking.add(userD2);
        ranking.add(userD3);

        pot.distribute(ranking);
        assertEquals(200, user1.getMoney());
        assertEquals(75, user2.getMoney());
        assertEquals(0, user3.getMoney());
        assertEquals(275, user4.getMoney());

    }

}