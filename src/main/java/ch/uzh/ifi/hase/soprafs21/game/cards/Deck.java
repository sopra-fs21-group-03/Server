package ch.uzh.ifi.hase.soprafs21.game.cards;

import ch.uzh.ifi.hase.soprafs21.constant.Rank;
import ch.uzh.ifi.hase.soprafs21.constant.Suit;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Deck implements Serializable {
    @ElementCollection
    private final List<Card> theDeck;

    public Deck() {
        theDeck = new ArrayList<Card>();
        for (Suit suit : Suit.values()){
            for(Rank rank: Rank.values()){
                var newCard = new Card(rank, suit);
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
        var drawCard = new Card(theDeck.get(0).getRank(), theDeck.get(0).getSuit());
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
