package ch.uzh.ifi.hase.soprafs21.helper;

import ch.uzh.ifi.hase.soprafs21.constant.Rank;
import ch.uzh.ifi.hase.soprafs21.constant.Suit;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.Pot;
import ch.uzh.ifi.hase.soprafs21.game.cards.Card;
import ch.uzh.ifi.hase.soprafs21.game.cards.River;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        GameEntity game = new GameEntity(1L);
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

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));

    }

    @Test
    void getRankingTwoPairBetterThanPair() throws Exception {
        GameEntity game = new GameEntity(1L);
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

        userOne.addCard(new Card(Suit.SPADE, Rank.FOUR));
        userOne.addCard(new Card(Suit.CLUB, Rank.KING));
        userTwo.addCard(new Card(Suit.SPADE, Rank.TWO));
        userTwo.addCard(new Card(Suit.HEART, Rank.KING));

        river.addCard(new Card(Suit.CLUB, Rank.TWO));
        river.addCard(new Card(Suit.SPADE, Rank.THREE));
        river.addCard(new Card(Suit.SPADE, Rank.SIX));
        river.addCard(new Card(Suit.DIAMOND, Rank.SEVEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.KING));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));

    }

    @Test
    void getRankingThreeBetterThanTwoPair() throws Exception {
        GameEntity game = new GameEntity(1L);
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
        river.addCard(new Card(Suit.DIAMOND, Rank.TWO));
        river.addCard(new Card(Suit.DIAMOND, Rank.KING));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));

    }

    @Test
    void getRankingStraightBetterThanThree() throws Exception {
        GameEntity game = new GameEntity(1L);
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

        userOne.addCard(new Card(Suit.SPADE, Rank.KING));
        userOne.addCard(new Card(Suit.CLUB, Rank.KING));
        userTwo.addCard(new Card(Suit.SPADE, Rank.FOUR));
        userTwo.addCard(new Card(Suit.HEART, Rank.FIVE));

        river.addCard(new Card(Suit.CLUB, Rank.TWO));
        river.addCard(new Card(Suit.SPADE, Rank.THREE));
        river.addCard(new Card(Suit.SPADE, Rank.SIX));
        river.addCard(new Card(Suit.DIAMOND, Rank.SEVEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.KING));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));

    }

    @Test
    void getRankingFlushBetterThanStraight() throws Exception {
        GameEntity game = new GameEntity(1L);
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

        userOne.addCard(new Card(Suit.SPADE, Rank.FOUR));
        userOne.addCard(new Card(Suit.CLUB, Rank.FIVE));
        userTwo.addCard(new Card(Suit.DIAMOND, Rank.QUEEN));
        userTwo.addCard(new Card(Suit.DIAMOND, Rank.EIGHT));

        river.addCard(new Card(Suit.DIAMOND, Rank.TWO));
        river.addCard(new Card(Suit.SPADE, Rank.THREE));
        river.addCard(new Card(Suit.SPADE, Rank.SIX));
        river.addCard(new Card(Suit.DIAMOND, Rank.SEVEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.KING));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));

    }

    @Test
    void getRankingFullHouseBetterThanFlush() throws Exception {
        GameEntity game = new GameEntity(1L);
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

        userOne.addCard(new Card(Suit.DIAMOND, Rank.ACE));
        userOne.addCard(new Card(Suit.DIAMOND, Rank.FIVE));
        userTwo.addCard(new Card(Suit.SPADE, Rank.TWO));
        userTwo.addCard(new Card(Suit.HEART, Rank.EIGHT));

        river.addCard(new Card(Suit.DIAMOND, Rank.TWO));
        river.addCard(new Card(Suit.SPADE, Rank.EIGHT));
        river.addCard(new Card(Suit.SPADE, Rank.SIX));
        river.addCard(new Card(Suit.DIAMOND, Rank.EIGHT));
        river.addCard(new Card(Suit.DIAMOND, Rank.KING));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));

    }

    @Test
    void getRankingFourBetterThanFullHouse() throws Exception {
        GameEntity game = new GameEntity(1L);
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

        userOne.addCard(new Card(Suit.SPADE, Rank.SEVEN));
        userOne.addCard(new Card(Suit.CLUB, Rank.SEVEN));
        userTwo.addCard(new Card(Suit.SPADE, Rank.TWO));
        userTwo.addCard(new Card(Suit.HEART, Rank.TWO));

        river.addCard(new Card(Suit.CLUB, Rank.TWO));
        river.addCard(new Card(Suit.SPADE, Rank.THREE));
        river.addCard(new Card(Suit.SPADE, Rank.SIX));
        river.addCard(new Card(Suit.DIAMOND, Rank.SEVEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.TWO));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));

    }

    @Test
    void getRankingStraightFlushBetterThanFour() throws Exception {
        GameEntity game = new GameEntity(1L);
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

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));

    }

    @Test
    void getRankingRoyalFLushBetterThanStraightFlush() throws Exception {
        GameEntity game = new GameEntity(1L);
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

        userOne.addCard(new Card(Suit.DIAMOND, Rank.NINE));
        userOne.addCard(new Card(Suit.CLUB, Rank.FIVE));
        userTwo.addCard(new Card(Suit.SPADE, Rank.TWO));
        userTwo.addCard(new Card(Suit.DIAMOND, Rank.ACE));

        river.addCard(new Card(Suit.CLUB, Rank.TWO));
        river.addCard(new Card(Suit.DIAMOND, Rank.TEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.JACK));
        river.addCard(new Card(Suit.DIAMOND, Rank.QUEEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.KING));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));

    }

    @Test
    void getRankingHighCard() throws Exception {
        GameEntity game = new GameEntity(1L);
        River river = new River();
        game.setRiver(river);
        CardRanking cardRanking = new CardRanking();
        Pot pot = new Pot();
        game.setPot(pot);

        User userOne = new User();
        userOne.setUsername("one");
        User userTwo = new User();
        userTwo.setUsername("two");
        User userThree = new User();
        userThree.setUsername("three");
        ArrayList<User> users = new ArrayList<>();
        users.add(userOne);
        users.add(userTwo);
        users.add(userThree);
        game.setAllUsers(users);
        game.setActiveUsers(users);
        pot.addUser(userOne);
        pot.addUser(userTwo);
        pot.addUser(userThree);
        pot.addMoney(userOne, 100);
        pot.addMoney(userTwo, 100);
        pot.addMoney(userThree, 100);

        userOne.addCard(new Card(Suit.DIAMOND, Rank.TWO));
        userOne.addCard(new Card(Suit.CLUB, Rank.THREE));
        userTwo.addCard(new Card(Suit.SPADE, Rank.TWO));
        userTwo.addCard(new Card(Suit.DIAMOND, Rank.ACE));
        userThree.addCard(new Card(Suit.DIAMOND, Rank.THREE));
        userThree.addCard(new Card(Suit.CLUB, Rank.TWO));

        river.addCard(new Card(Suit.CLUB, Rank.SEVEN));
        river.addCard(new Card(Suit.SPADE, Rank.NINE));
        river.addCard(new Card(Suit.HEART, Rank.TEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.QUEEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.KING));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        userDraw.addUser(userThree, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));
    }

    @Test
    void getRankingPair() throws Exception {
        GameEntity game = new GameEntity(1L);
        River river = new River();
        game.setRiver(river);
        CardRanking cardRanking = new CardRanking();
        Pot pot = new Pot();
        game.setPot(pot);

        User userOne = new User();
        userOne.setUsername("one");
        User userTwo = new User();
        userTwo.setUsername("two");
        User userThree = new User();
        userThree.setUsername("three");
        ArrayList<User> users = new ArrayList<>();
        users.add(userOne);
        users.add(userTwo);
        users.add(userThree);
        game.setAllUsers(users);
        game.setActiveUsers(users);
        pot.addUser(userOne);
        pot.addUser(userTwo);
        pot.addUser(userThree);
        pot.addMoney(userOne, 100);
        pot.addMoney(userTwo, 100);
        pot.addMoney(userThree, 100);

        userOne.addCard(new Card(Suit.DIAMOND, Rank.TEN));
        userOne.addCard(new Card(Suit.CLUB, Rank.THREE));
        userTwo.addCard(new Card(Suit.SPADE, Rank.SEVEN));
        userTwo.addCard(new Card(Suit.DIAMOND, Rank.ACE));
        userThree.addCard(new Card(Suit.DIAMOND, Rank.THREE));
        userThree.addCard(new Card(Suit.CLUB, Rank.TEN));

        river.addCard(new Card(Suit.CLUB, Rank.SEVEN));
        river.addCard(new Card(Suit.SPADE, Rank.NINE));
        river.addCard(new Card(Suit.HEART, Rank.TEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.QUEEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.KING));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        userDraw.addUser(userThree, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);


        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));
    }

    @Test
    void getRankingTwoPair() throws Exception {
        GameEntity game = new GameEntity(1L);
        River river = new River();
        game.setRiver(river);
        CardRanking cardRanking = new CardRanking();
        Pot pot = new Pot();
        game.setPot(pot);

        User userOne = new User();
        userOne.setUsername("one");
        User userTwo = new User();
        userTwo.setUsername("two");
        User userThree = new User();
        userThree.setUsername("three");
        ArrayList<User> users = new ArrayList<>();
        users.add(userOne);
        users.add(userTwo);
        users.add(userThree);
        game.setAllUsers(users);
        game.setActiveUsers(users);
        pot.addUser(userOne);
        pot.addUser(userTwo);
        pot.addUser(userThree);
        pot.addMoney(userOne, 100);
        pot.addMoney(userTwo, 100);
        pot.addMoney(userThree, 100);

        userOne.addCard(new Card(Suit.DIAMOND, Rank.TWO));
        userOne.addCard(new Card(Suit.CLUB, Rank.THREE));
        userTwo.addCard(new Card(Suit.SPADE, Rank.TWO));
        userTwo.addCard(new Card(Suit.DIAMOND, Rank.ACE));
        userThree.addCard(new Card(Suit.DIAMOND, Rank.THREE));
        userThree.addCard(new Card(Suit.CLUB, Rank.TWO));

        river.addCard(new Card(Suit.CLUB, Rank.ACE));
        river.addCard(new Card(Suit.SPADE, Rank.TWO));
        river.addCard(new Card(Suit.HEART, Rank.THREE));
        river.addCard(new Card(Suit.DIAMOND, Rank.QUEEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.KING));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        userDraw.addUser(userThree, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));
    }

    @Test
    void getRankingThree() throws Exception {
        GameEntity game = new GameEntity(1L);
        River river = new River();
        game.setRiver(river);
        CardRanking cardRanking = new CardRanking();
        Pot pot = new Pot();
        game.setPot(pot);

        User userOne = new User();
        userOne.setUsername("one");
        User userTwo = new User();
        userTwo.setUsername("two");
        User userThree = new User();
        userThree.setUsername("three");
        ArrayList<User> users = new ArrayList<>();
        users.add(userOne);
        users.add(userTwo);
        users.add(userThree);
        game.setAllUsers(users);
        game.setActiveUsers(users);
        pot.addUser(userOne);
        pot.addUser(userTwo);
        pot.addUser(userThree);
        pot.addMoney(userOne, 100);
        pot.addMoney(userTwo, 100);
        pot.addMoney(userThree, 100);

        userOne.addCard(new Card(Suit.DIAMOND, Rank.TWO));
        userOne.addCard(new Card(Suit.CLUB, Rank.THREE));
        userTwo.addCard(new Card(Suit.SPADE, Rank.SEVEN));
        userTwo.addCard(new Card(Suit.DIAMOND, Rank.SEVEN));
        userThree.addCard(new Card(Suit.DIAMOND, Rank.THREE));
        userThree.addCard(new Card(Suit.CLUB, Rank.TWO));

        river.addCard(new Card(Suit.CLUB, Rank.SEVEN));
        river.addCard(new Card(Suit.SPADE, Rank.TWO));
        river.addCard(new Card(Suit.HEART, Rank.TWO));
        river.addCard(new Card(Suit.DIAMOND, Rank.QUEEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.KING));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        userDraw.addUser(userThree, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));
    }

    @Test
    void getRankingStraight() throws Exception {
        GameEntity game = new GameEntity(1L);
        River river = new River();
        game.setRiver(river);
        CardRanking cardRanking = new CardRanking();
        Pot pot = new Pot();
        game.setPot(pot);

        User userOne = new User();
        userOne.setUsername("one");
        User userTwo = new User();
        userTwo.setUsername("two");
        User userThree = new User();
        userThree.setUsername("three");
        ArrayList<User> users = new ArrayList<>();
        users.add(userOne);
        users.add(userTwo);
        users.add(userThree);
        game.setAllUsers(users);
        game.setActiveUsers(users);
        pot.addUser(userOne);
        pot.addUser(userTwo);
        pot.addUser(userThree);
        pot.addMoney(userOne, 100);
        pot.addMoney(userTwo, 100);
        pot.addMoney(userThree, 100);

        userOne.addCard(new Card(Suit.DIAMOND, Rank.TWO));
        userOne.addCard(new Card(Suit.CLUB, Rank.NINE));
        userTwo.addCard(new Card(Suit.SPADE, Rank.TWO));
        userTwo.addCard(new Card(Suit.DIAMOND, Rank.ACE));
        userThree.addCard(new Card(Suit.DIAMOND, Rank.NINE));
        userThree.addCard(new Card(Suit.CLUB, Rank.TWO));

        river.addCard(new Card(Suit.CLUB, Rank.SEVEN));
        river.addCard(new Card(Suit.SPADE, Rank.TEN));
        river.addCard(new Card(Suit.HEART, Rank.JACK));
        river.addCard(new Card(Suit.DIAMOND, Rank.QUEEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.KING));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        userDraw.addUser(userThree, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));
    }

    @Test
    void getRankingFlush() throws Exception {
        GameEntity game = new GameEntity(1L);
        River river = new River();
        game.setRiver(river);
        CardRanking cardRanking = new CardRanking();
        Pot pot = new Pot();
        game.setPot(pot);

        User userOne = new User();
        userOne.setUsername("one");
        User userTwo = new User();
        userTwo.setUsername("two");
        User userThree = new User();
        userThree.setUsername("three");
        ArrayList<User> users = new ArrayList<>();
        users.add(userOne);
        users.add(userTwo);
        users.add(userThree);
        game.setAllUsers(users);
        game.setActiveUsers(users);
        pot.addUser(userOne);
        pot.addUser(userTwo);
        pot.addUser(userThree);
        pot.addMoney(userOne, 100);
        pot.addMoney(userTwo, 100);
        pot.addMoney(userThree, 100);

        userOne.addCard(new Card(Suit.DIAMOND, Rank.TWO));
        userOne.addCard(new Card(Suit.CLUB, Rank.THREE));
        userTwo.addCard(new Card(Suit.SPADE, Rank.TWO));
        userTwo.addCard(new Card(Suit.DIAMOND, Rank.ACE));
        userThree.addCard(new Card(Suit.DIAMOND, Rank.THREE));
        userThree.addCard(new Card(Suit.CLUB, Rank.TWO));

        river.addCard(new Card(Suit.DIAMOND, Rank.SEVEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.NINE));
        river.addCard(new Card(Suit.HEART, Rank.TEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.QUEEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.KING));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userThree, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        expected.add(userDraw);


        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));
        assertEquals(expected.get(2), actual.get(2));
    }

    @Test
    void getRankingFullHouse() throws Exception {
        GameEntity game = new GameEntity(1L);
        River river = new River();
        game.setRiver(river);
        CardRanking cardRanking = new CardRanking();
        Pot pot = new Pot();
        game.setPot(pot);

        User userOne = new User();
        userOne.setUsername("one");
        User userTwo = new User();
        userTwo.setUsername("two");
        User userThree = new User();
        userThree.setUsername("three");
        ArrayList<User> users = new ArrayList<>();
        users.add(userOne);
        users.add(userTwo);
        users.add(userThree);
        game.setAllUsers(users);
        game.setActiveUsers(users);
        pot.addUser(userOne);
        pot.addUser(userTwo);
        pot.addUser(userThree);
        pot.addMoney(userOne, 100);
        pot.addMoney(userTwo, 100);
        pot.addMoney(userThree, 100);

        userOne.addCard(new Card(Suit.DIAMOND, Rank.TWO));
        userOne.addCard(new Card(Suit.CLUB, Rank.THREE));
        userTwo.addCard(new Card(Suit.SPADE, Rank.TWO));
        userTwo.addCard(new Card(Suit.DIAMOND, Rank.ACE));
        userThree.addCard(new Card(Suit.DIAMOND, Rank.THREE));
        userThree.addCard(new Card(Suit.CLUB, Rank.TWO));

        river.addCard(new Card(Suit.HEART, Rank.TWO));
        river.addCard(new Card(Suit.SPADE, Rank.THREE));
        river.addCard(new Card(Suit.HEART, Rank.TWO));
        river.addCard(new Card(Suit.HEART, Rank.ACE));
        river.addCard(new Card(Suit.CLUB, Rank.ACE));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        userDraw.addUser(userThree, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));
    }

    //Four of a kind can only have one user at a time.

    @Test
    void getRankingStraightFLush() throws Exception {
        GameEntity game = new GameEntity(1L);
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

        userOne.addCard(new Card(Suit.DIAMOND, Rank.THREE));
        userOne.addCard(new Card(Suit.CLUB, Rank.FIVE));
        userTwo.addCard(new Card(Suit.DIAMOND, Rank.EIGHT));
        userTwo.addCard(new Card(Suit.HEART, Rank.EIGHT));

        river.addCard(new Card(Suit.CLUB, Rank.TWO));
        river.addCard(new Card(Suit.DIAMOND, Rank.FOUR));
        river.addCard(new Card(Suit.DIAMOND, Rank.FIVE));
        river.addCard(new Card(Suit.DIAMOND, Rank.SEVEN));
        river.addCard(new Card(Suit.DIAMOND, Rank.SIX));

        List<UserDraw> actual = cardRanking.getRanking(game);
        List<UserDraw> expected = new ArrayList<>();
        UserDraw userDraw = new UserDraw();
        userDraw.addUser(userTwo, 100);
        expected.add(userDraw);
        userDraw = new UserDraw();
        userDraw.addUser(userOne, 100);
        expected.add(userDraw);

        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));

    }

    //Royal Flush can only have one user at a time.

}