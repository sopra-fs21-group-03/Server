package ch.uzh.ifi.hase.soprafs21.game.cards;

import ch.uzh.ifi.hase.soprafs21.constant.Rank;
import ch.uzh.ifi.hase.soprafs21.constant.Suit;

import javax.persistence.Embeddable;
import java.util.ArrayList;

@Embeddable
public class Deck {
    private ArrayList<Card> theDeck;

    public Deck() {
        theDeck = new ArrayList<Card>();
        for (Suit suit : Suit.values()){
            for(Rank rank: Rank.values()){
                Card newCard = new Card(rank, suit);
                theDeck.add(newCard);
            }
        }
    }

    public ArrayList<Card> giveDeck(){
        return theDeck;
    }



}
