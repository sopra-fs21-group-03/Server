package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;

@Entity
@Table(name = "GAME")
public class GameEntity {

    @Id
    @GeneratedValue
    private Long gameID;

    public void setGameID(Long gameID) {
        this.gameID = gameID;
    }

    public Long getGameID() {
        return gameID;
    }
}
