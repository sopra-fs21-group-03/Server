package ch.uzh.ifi.hase.soprafs21.game.cards;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Embeddable
public class River implements Serializable {

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Card> cards = new ArrayList<>();

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public void addCard(Card card) throws IllegalStateException {
        if(this.cards.size() >= 5) {
            throw new IllegalStateException("River is full, can't add more cards.");
        }
        this.cards.add(card);
    }

    public void clear() {
        cards.clear();
    }
}
