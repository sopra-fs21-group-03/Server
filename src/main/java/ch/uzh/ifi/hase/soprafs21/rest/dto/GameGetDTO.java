package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.Pot;
import ch.uzh.ifi.hase.soprafs21.game.cards.River;

public class GameGetDTO {

    private String gameName;
    private River river;
    private Pot pot;
    private boolean showdown;
    private User onTurn;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public River getRiver() {
        return river;
    }

    public void setRiver(River river) {
        this.river = river;
    }

    public Pot getPot() {
        return pot;
    }

    public void setPot(Pot pot) {
        this.pot = pot;
    }

    public boolean isShowdown() {
        return showdown;
    }

    public void setShowdown(boolean showdown) {
        this.showdown = showdown;
    }

    public User getOnTurn() {
        return onTurn;
    }

    public void setOnTurn(User onTurn) {
        this.onTurn = onTurn;
    }

}
