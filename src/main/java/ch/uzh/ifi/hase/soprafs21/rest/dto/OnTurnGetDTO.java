package ch.uzh.ifi.hase.soprafs21.rest.dto;

import javax.persistence.Embeddable;

@Embeddable
public class OnTurnGetDTO {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
