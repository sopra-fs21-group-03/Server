package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class UserLoginGetDTO {
    private String token;
    private Integer gameId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }
}
