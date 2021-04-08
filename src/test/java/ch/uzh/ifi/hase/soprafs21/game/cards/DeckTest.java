package ch.uzh.ifi.hase.soprafs21.game.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    Deck testdeck;

    @BeforeEach
    public void setup() {
        testdeck = new Deck();
    }

    @Test
    public void drawTest() {
        try {
            /**
             * Give my a Card (draw it)
             */
            Card myCard = testdeck.draw();

            /**
             * For all Cards that are still in the deck, this card should not appear again
             */

            ArrayList<Card> doesNotContainMyCard = new ArrayList<Card>();

            Card notMyCard = testdeck.draw();
            doesNotContainMyCard.add(notMyCard);

            while (testdeck.size() > 0) {
                notMyCard = testdeck.draw();
                doesNotContainMyCard.add(notMyCard);
            }

            for (Card card : doesNotContainMyCard) {
                if (card.getRank() == myCard.getRank() && card.getSuit() == myCard.getSuit()) {
                    fail();
                }
            }


        }
        catch (Exception e) {
            fail();
        }


    }

    @Test
    public void drawTest_Exception_DeckIsEmpty() {
        try {

            /**
             * You use draw() too many times -> the Deck will be empty and an Exception should be thrown
             */

            ArrayList<Card> myCards = new ArrayList<Card>();

            Card theCard = testdeck.draw();
            myCards.add(theCard);

            while (testdeck.size() >= 0) {
                if (testdeck.size() > 0) {
                    theCard = testdeck.draw();
                    myCards.add(theCard);
                }
                else if (testdeck.size() == 0) {
                    //draw too much
                    assertThrows(Exception.class, () -> testdeck.draw());
                    break;

                }

            }


        }
        catch (Exception e) {
            fail();
        }


    }


}