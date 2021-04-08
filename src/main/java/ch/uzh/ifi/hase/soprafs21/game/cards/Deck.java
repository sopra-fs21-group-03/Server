package ch.uzh.ifi.hase.soprafs21.game.cards;

import ch.uzh.ifi.hase.soprafs21.constant.Rank;
import ch.uzh.ifi.hase.soprafs21.constant.Suit;

import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.Collections;

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
        shuffle();
    }

    public int size(){
        return theDeck.size();
    }

    public Card draw() throws Exception {
        if(size() > 0){
        Card drawCard = new Card(theDeck.get(0).getRank(), theDeck.get(0).getSuit());
        theDeck.remove(0);
        return drawCard;}
        else{
            throw new Exception("No Cards are left!");
        }
    }


    public void shuffle(){
        Collections.shuffle(theDeck);
    }



}
