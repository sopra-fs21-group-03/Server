package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.Round;
import ch.uzh.ifi.hase.soprafs21.game.Pot;
import ch.uzh.ifi.hase.soprafs21.game.cards.River;

import java.util.List;

public class GameGetDTO {

    private String gameName;
    private River river;
    private Pot pot;
    private boolean showdown;
    private OnTurnGetDTO onTurn;
    private Round round;

    private List<OpponentInGameGetDTO> opponents;

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

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

    public OnTurnGetDTO getOnTurn() {
        return onTurn;
    }

    public void setOnTurn(OnTurnGetDTO onTurn) {
        this.onTurn = onTurn;
    }

    public List<OpponentInGameGetDTO> getOpponents() {
        return opponents;
    }

    public void setOpponents(List<OpponentInGameGetDTO> opponents) {
        this.opponents = opponents;
    }
}
