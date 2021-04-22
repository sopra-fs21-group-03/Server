package ch.uzh.ifi.hase.soprafs21.game.protocol;

import javax.persistence.ElementCollection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChatLogger {
    @ElementCollection
    private Map<String, String> chatLoggerElements;

    public ChatLogger(){
        chatLoggerElements = new LinkedHashMap<>();
    }

    public String getChatLoggerElements(){
        return null;
    }
}
