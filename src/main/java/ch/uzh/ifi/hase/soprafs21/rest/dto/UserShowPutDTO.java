package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class UserShowPutDTO {
    private String token;
    private boolean wantsToShow;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isWantsToShow() {
        return wantsToShow;
    }

    public void setWantsToShow(boolean wantsToShow) {
        this.wantsToShow = wantsToShow;
    }
}
