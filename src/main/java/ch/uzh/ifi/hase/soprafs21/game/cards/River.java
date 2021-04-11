package ch.uzh.ifi.hase.soprafs21.game.cards;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.List;


@Embeddable
public class River {

    @ElementCollection
    private List<Card> cards = new ArrayList<>();

    public ArrayList<Card> getCards() {
        return new ArrayList<>(cards);
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
