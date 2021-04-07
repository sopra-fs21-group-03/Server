package ch.uzh.ifi.hase.soprafs21.game.cards;

import javassist.bytecode.ExceptionTable;

import java.util.ArrayList;

public class River {

    private ArrayList<Card> cards;

    public ArrayList<Card> getCards() {
        return (ArrayList<Card>) cards.clone();
    }

    public void addCards(Card card) throws Exception {
        if(this.cards.size() >= 5) {
            throw new Exception("River is full, can't add more cards.");
        }
        this.cards.add(card);
    }

    public void clear() {
        cards.clear();
    }
}
