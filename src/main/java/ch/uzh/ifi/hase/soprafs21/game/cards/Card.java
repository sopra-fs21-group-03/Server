package ch.uzh.ifi.hase.soprafs21.game.cards;

import ch.uzh.ifi.hase.soprafs21.constant.Rank;
import ch.uzh.ifi.hase.soprafs21.constant.Suit;

public class Card {
    private final Rank myRank;
    private final Suit mySuit;

    public Card(Rank myRank, Suit mySuit) {
        this.myRank = myRank;
        this.mySuit = mySuit;
    }

    public Rank getRank(){
        return myRank;
    }

    public Suit getSuit(){
        return mySuit;
    }
}
