package ch.uzh.ifi.hase.soprafs21.game.cards;

import ch.uzh.ifi.hase.soprafs21.constant.Rank;
import ch.uzh.ifi.hase.soprafs21.constant.Suit;

public class Card {
    public final Rank myRank;
    public final Suit mySuit;

    public Card(Rank myRank, Suit mySuit) {
        this.myRank = myRank;
        this.mySuit = mySuit;
    }
}
