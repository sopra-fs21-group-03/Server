package ch.uzh.ifi.hase.soprafs21.game.protocol;

import ch.uzh.ifi.hase.soprafs21.constant.MessageType;
import ch.uzh.ifi.hase.soprafs21.entity.Name;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Entity
@Table(name = "PROTOCOL")
public class ProtocolElement implements Serializable {
    //@ElementCollection
    //private Map<String, String> chatLoggerElements;

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private MessageType messageType;

    @Column
    private String name;

    @Column
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
