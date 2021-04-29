package ch.uzh.ifi.hase.soprafs21.game.protocol;

import ch.uzh.ifi.hase.soprafs21.constant.MessageType;
import ch.uzh.ifi.hase.soprafs21.entity.Name;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class ProtocolElement implements Serializable {

    private MessageType messageType;

    private String name;

    private String message;

    public ProtocolElement() {
    }

    public ProtocolElement(MessageType messageType, Name object, String message){
        this.messageType = messageType;
        this.name = object.getName();
        this.message = message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
