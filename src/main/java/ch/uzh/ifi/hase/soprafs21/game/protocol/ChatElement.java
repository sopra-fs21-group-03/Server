package ch.uzh.ifi.hase.soprafs21.game.protocol;

import ch.uzh.ifi.hase.soprafs21.entity.User;

public class ChatElement implements ProtocolElement{
    private User user;
    private String message;

    public ChatElement(User user, String message){
        this.user = user;
        this.message = message;
    }

    @Override
    public String getAsString() {
        return user.getUsername()+ " " +message;
    }
}
