package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.List;

public class SpecificLobbyGetDTO {

    private String name;
    private List<PlayerInLobbyGetDTO> players;
    private boolean gameCanStart;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PlayerInLobbyGetDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerInLobbyGetDTO> players) {
        this.players = players;
    }

    public boolean isGameCanStart() {
        return gameCanStart;
    }

    public void setGameCanStart(boolean gameCanStart) {
        this.gameCanStart = gameCanStart;
    }
}
