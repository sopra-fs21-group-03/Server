package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.Blind;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class OpponentInGameGetDTO implements Serializable {
    private String username;
    private int money;
    private Blind blind;
    private boolean inGame;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Blind getBlind() {
        return blind;
    }

    public void setBlind(Blind blind) {
        this.blind = blind;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }
}
