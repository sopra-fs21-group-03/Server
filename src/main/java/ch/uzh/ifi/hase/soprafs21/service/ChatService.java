package ch.uzh.ifi.hase.soprafs21.service;


import ch.uzh.ifi.hase.soprafs21.constant.MessageType;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.protocol.ProtocolElement;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatService {
    private final GameRepository gameRepository;

    private static final String NOT_FOUND_MESSAGE = "The User could not be found...";

    /**
     * @param gameRepository this is the Repository which the ChatService will receive. Since the ChatService is responsible for logs related to saved
     *                       games, we need a Repository that saves Games.
     */
    @Autowired
    public ChatService(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }
    public GameEntity findGameEntity(Long gameid) {
        Optional<GameEntity> potentialGame = gameRepository.findById(gameid);
        GameEntity theGame = null;
        if (potentialGame.isPresent()) {
            theGame = potentialGame.get();
            return theGame;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The GameSession could not be found...");
        }
    }
    public User getUserInGameById(GameEntity theGame, Long userId) {
        for (User user : theGame.getRawPlayersInTurnOrder()) {
            if (userId.equals(user.getId())) {
                return user;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE);
    }

    public List<ProtocolElement> getProtocol(Long gameId) {
        var game = findGameEntity(gameId);
        return game.getProtocol();
    }

    public void addChatMessage(GameEntity game, User chatterUser, String chatMessage){
        var element = new ProtocolElement(MessageType.CHAT, chatterUser, String.format("User %s says: %s", chatterUser.getUsername(), chatMessage));
        game.addProtocolElement(element);
        gameRepository.save(game);

    }



}
