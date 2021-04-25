package ch.uzh.ifi.hase.soprafs21.rest.dto;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class OnTurnGetDTO implements Serializable {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
