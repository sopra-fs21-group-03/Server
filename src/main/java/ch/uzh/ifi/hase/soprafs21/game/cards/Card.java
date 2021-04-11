package ch.uzh.ifi.hase.soprafs21.game.cards;

import ch.uzh.ifi.hase.soprafs21.constant.Rank;
import ch.uzh.ifi.hase.soprafs21.constant.Suit;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Card implements Serializable {
    private final Rank myRank;
    private final Suit mySuit;

    public Card(Rank myRank, Suit mySuit) {
        this.myRank = myRank;
        this.mySuit = mySuit;
    }

    public Card(Suit suit, Rank rank) {
        this.mySuit = suit;
        this.myRank = rank;
    }

    /**
     * Default constructor so hibernate can save cards in the repo
     */
    public Card() {
        this.myRank = null;
        this.mySuit = null;
    }

    public Rank getRank(){
        return myRank;
    }


    public Suit getSuit() {
        return mySuit;
    }
}
