package ch.uzh.ifi.hase.soprafs21.helper;

import ch.uzh.ifi.hase.soprafs21.constant.Rank;
import ch.uzh.ifi.hase.soprafs21.constant.Suit;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.Pot;
import ch.uzh.ifi.hase.soprafs21.game.cards.Card;
import ch.uzh.ifi.hase.soprafs21.game.cards.River;
import org.hibernate.type.descriptor.java.CharacterArrayTypeDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CardRankingTest {

/*
    @Test
    void getRankingBothHighCard() throws Exception {
        GameEntity game = new GameEntity();
        River river = new River();
        game.setRiver(river);
        CardRanking cardRanking = new CardRanking();
        Pot pot = new Pot();
        game.setPot(pot);

        User userOne = new User();
        userOne.setUsername("one");
        User userTwo = new User();
        userTwo.setUsername("two");
        ArrayList<User> users = new ArrayList<>();
        users.add(userOne);
        users.add(userTwo);
        game.setAllUsers(users);
        game.setActiveUsers(users);
        pot.addUser(userOne);
        pot.addUser(userTwo);
        pot.addMoney(userOne, 100);
        pot.addMoney(userTwo, 100);

        userOne.addCard(new Card(Suit.SPADE, Rank.ACE));
        userOne.addCard(new Card(Suit.CLUB, Rank.FIVE));
        userTwo.addCard(new Card(Suit.SPADE, Rank.QUEEN));
        userTwo.addCard(new Card(Suit.HEART, Rank.EIGHT));

        river.addCard(new Card(Suit.CLUB, Rank.TWO));
        river.addCard(new Card(Suit.SPADE, Rank.THREE));
        river.addCard(new Card(Suit.SPADE, Rank.SIX));
        river.addCard(new Card(Suit.DIAMOND, Rank.SEVEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.KING));

        ArrayList<UserDraw> actual = cardRanking.getRanking(game);
        ArrayList<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));

    }
*/

    @Test
    void getRankingPairBetterThanHighCard() throws Exception {
        GameEntity game = new GameEntity();
        River river = new River();
        game.setRiver(river);
        CardRanking cardRanking = new CardRanking();
        Pot pot = new Pot();
        game.setPot(pot);

        User userOne = new User();
        userOne.setUsername("one");
        User userTwo = new User();
        userTwo.setUsername("two");
        ArrayList<User> users = new ArrayList<>();
        users.add(userOne);
        users.add(userTwo);
        game.setAllUsers(users);
        game.setActiveUsers(users);
        pot.addUser(userOne);
        pot.addUser(userTwo);
        pot.addMoney(userOne, 100);
        pot.addMoney(userTwo, 100);

        userOne.addCard(new Card(Suit.SPADE, Rank.ACE));
        userOne.addCard(new Card(Suit.CLUB, Rank.FIVE));
        userTwo.addCard(new Card(Suit.SPADE, Rank.TWO));
        userTwo.addCard(new Card(Suit.HEART, Rank.EIGHT));

        river.addCard(new Card(Suit.CLUB, Rank.TWO));
        river.addCard(new Card(Suit.SPADE, Rank.THREE));
        river.addCard(new Card(Suit.SPADE, Rank.SIX));
        river.addCard(new Card(Suit.DIAMOND, Rank.SEVEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.KING));

        ArrayList<UserDraw> actual = cardRanking.getRanking(game);
        ArrayList<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));

    }
}