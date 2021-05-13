package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class PlayerInLobbyGetDTO implements Serializable {
    private String username;
    private GameStatus readyStatus;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GameStatus getReadyStatus() {
        return readyStatus;
    }

    public void setReadyStatus(GameStatus readyStatus) {
        this.readyStatus = readyStatus;
    }
}
