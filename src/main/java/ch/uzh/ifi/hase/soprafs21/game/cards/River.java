package ch.uzh.ifi.hase.soprafs21.game.cards;


import javassist.bytecode.ExceptionTable;

import javax.persistence.Embeddable;
import java.util.ArrayList;


@Embeddable
public class River {

    private ArrayList<Card> cards = new ArrayList<>();

    public ArrayList<Card> getCards() {
        return (ArrayList<Card>) cards.clone();
    }

    public void addCard(Card card) throws Exception {
        if(this.cards.size() >= 5) {
            throw new Exception("River is full, can't add more cards.");
        }
        this.cards.add(card);
    }

    public void clear() {
        cards.clear();
    }
}
