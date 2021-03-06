package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.PlayerInLobbyGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LobbyService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GameService gameService;
    private static final String NOT_FOUND_MESSAGE = "The User could not be found...";

    @Autowired
    public LobbyService(@Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("userRepository") UserRepository userRepository, GameService gameService) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.gameService = gameService;
    }

    /**
     * @param gameid The id of the Game that should be analyzed
     * @return the GameEntity with the corresponding gameid, if it exists. Else, if there is no game with such an id, a ResponseStatusException will be thrown.
     */
    public GameEntity findGameEntity(Long gameid) {
        Optional<GameEntity> potentialGame = gameRepository.findById(gameid);
        GameEntity theGame;
        if (potentialGame.isPresent()) {
            theGame = potentialGame.get();
            return theGame;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The GameSession could not be found...");
        }
    }


    public void checkIfUserExistsByToken(String token) {
        Optional<User> potentialUser = Optional.ofNullable(userRepository.findByToken(token));
        if (potentialUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE);
        }
    }


    public List<GameEntity> getAllGames() {
        return gameRepository.findAll();
    }


    /**
     * Helper Function
     * Used to add a registered/logged in user to a GameEntity
     *
     * @param userToBeAdded user that should be added to the game list
     */
    public void addUserToGame(User userToBeAdded, GameEntity gameEntity) {
        if (!gameEntity.getInGame()) {
            if (gameEntity.getAllUsers().size() < 5) {
                if (!gameEntity.getAllUsers().contains(userToBeAdded) && !gameEntity.getSpectators().contains(userToBeAdded)) {
                    gameEntity.addUserToAll(userToBeAdded);
                    gameEntity.addUserToActive(userToBeAdded);

                    // Convert long to Integer & add it to user
                    userToBeAdded.setGameId(gameEntity.getId().intValue());
                    userRepository.save(userToBeAdded);
                    gameRepository.save(gameEntity);
                    gameRepository.flush();
                    userRepository.flush();
                }
            }
            else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "You can not join this Lobby, since the Session is already full!");
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You can not join this Lobby, since the Session has already started and the other Users are playing!");
        }
    }

    public User getUserInSpecificGameSessionInAllUsers(Long userID, GameEntity gameEntity) {
        for (User user : gameEntity.getActiveUsers()) {
            if (userID.equals(user.getId())) {
                return user;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE);

    }


    public User getUserByTokenInUserRepository(String token) {
        return userRepository.findByToken(token);
    }

    public void setUserToReady(User userFound) {
        userFound.setGamestatus(GameStatus.READY);
        userRepository.saveAndFlush(userFound);
    }

    public void setUserToUnready(User userFound) {
        userFound.setGamestatus(GameStatus.NOTREADY);
        userRepository.saveAndFlush(userFound);
    }

    public void leaveLobby(User userFound, GameEntity gameEntity) {
        if (!gameEntity.getInGame()) {
            if (!gameEntity.getAllUsers().contains(userFound)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You can not leave this lobby since you are not part of this lobby to begin with");
            }
            userFound.setGamestatus(GameStatus.NOTREADY);
            userFound.setGameId(null);
            userRepository.saveAndFlush(userFound);

            gameEntity.removeUserFromActive(userFound.getId());
            gameEntity.removeUserFromAll(userFound.getId());
            gameRepository.save(gameEntity);
            gameRepository.flush();

        }
        else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You can not leave this Lobby since the Session has already started and the other Users are playing, pls leave with the game leave mapping");
        }
    }

    /**
     * Gets the specific lobby data for a lobby
     *
     * @param lobbyID ID of the lobby we would like to fetch
     * @return GameEntity of the requested lobby
     */
    public GameEntity getSpecificLobbyData(Long lobbyID) {
        var game = findGameEntity(lobbyID);
        ArrayList<PlayerInLobbyGetDTO> lobbyplayers = new ArrayList<>();
        for (User user : game.getAllUsers()) {
            lobbyplayers.add(DTOMapper.INSTANCE.convertEntityToPlayerInLobbyGetDTO(user));
        }
        for (User user2 : game.getSpectators()) {
            lobbyplayers.add(DTOMapper.INSTANCE.convertEntityToPlayerInLobbyGetDTO(user2));
        }
        game.setLobbyplayers(lobbyplayers);
        return game;
    }

    public void checkIfUserIsInGameSession(String token, GameEntity game) {
        for (User user : game.getAllUsers()) {
            if (user.getToken().equals(token)) {
                return;
            }
        }
        for (User user2 : game.getSpectators()) {
            if (user2.getToken().equals(token)) {
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not registered in the Lobby Session and therefore is not allowed to get more data about the Lobby!");
    }

    public void checkIfUserIsAlreadyInAnOtherLobby(String token, Long lobbyID, List<GameEntity> gameList) {
        for (GameEntity entity : gameList) {
            if (!entity.getId().equals(lobbyID)) {
                checkAllUsers(entity, token);
                checkSpectators(entity, token);
            }
        }
    }

    private void checkAllUsers(GameEntity entity, String token) {
        for (User user : entity.getAllUsers()) {
            if (user.getToken().equals(token)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The User is playing in an other Lobby and therefore can not join this Lobby!");
            }
        }
    }

    private void checkSpectators(GameEntity entity, String token) {
        for (User user2 : entity.getSpectators()) {
            if (user2.getToken().equals(token)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The User is a Spectator in an other Lobby and therefore can not join this Lobby!");
            }
        }
    }


    public void setUpGame(GameEntity game) {
        // Check if there are already five players in the game
        if (game.getGameCanStart() && game.isFirstGameSetup()) {
            try {
                game.setup();
                gameRepository.saveAndFlush(game);
                gameService.startTurnTimer(game.getId());
            }
            catch (Exception ignored) {

            }
        }
    }

}
