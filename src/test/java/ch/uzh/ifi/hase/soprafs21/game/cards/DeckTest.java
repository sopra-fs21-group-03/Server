package ch.uzh.ifi.hase.soprafs21.game.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    Deck testdeck;

    @BeforeEach
    public void setup() {
        testdeck = new Deck();
    }

    @Test
    private void setupTest(){
        for (Card myCard : testdeck.giveDeck()){
            System.out.println(myCard.myRank);
            System.out.println(myCard.mySuit);

        }
    }



}