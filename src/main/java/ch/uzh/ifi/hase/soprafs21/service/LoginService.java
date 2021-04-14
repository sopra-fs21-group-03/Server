package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * LoginService
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class LoginService {

    private final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @Autowired
    public LoginService(@Qualifier("userRepository") UserRepository userRepository, @Qualifier("gameRepository") GameRepository gameRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User getUserId(String token){
        return userRepository.findByToken(token);
    }


    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);

        newUser.setGamestatus(GameStatus.NOTREADY);
        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        /*
           Create Game and add User to the GameList
           or just add user to the GameList if game already exists
         */
        setUpGame(newUser);

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * This function checks the login credentials of a user,
     * throws a 401 UNAUTHORIZED error if the password or username is false
     *
     * @param userToLogin Converted UserInput of the user who wants to login
     * @return Token of the found user if credentials are checked successfully
     */
    public String checkLoginCredentials(User userToLogin) {
        User fetched = userRepository.findByUsername(userToLogin.getUsername());

        // Check if password and username match
        boolean valid = fetched != null && fetched.getPassword().equals(userToLogin.getPassword());

        // throw new Exception if they don't match
        if (!valid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password false");
        }

        fetched.setStatus(UserStatus.ONLINE);
        userRepository.save(fetched);

        /*
         if game already exists, simply add the user
         if game does not exist, create new game and add the user
        */
        setUpGame(fetched);

        return fetched.getToken();
    }

    /**
     * This method checks if the token matches the found user
     * If matching it logs the user out
     * If it doesn't find a user with this ID it throws the NOT_FOUND exception
     * If the token does not match, it throws an UNAUTHORIZED exception
     *
     * @param userToLogout used to get the token of the user who wants to log out
     */
    public void getUserToLogout(User userToLogout, Long userID) {
        Optional<User> fetched = userRepository.findById(userID);

        // If no user is found in the repo
        if (fetched.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        User fetchedEntity = fetched.get();

        // If the tokens do not match
        if (!fetchedEntity.getToken().equals(userToLogout.getToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Optional<GameEntity> optionalGame = gameRepository.findById(1L);

        // Try to delete user if game is still running
        optionalGame.ifPresent(gameEntity -> deleteUserFromGame(userID, gameEntity));

        fetchedEntity.setStatus(UserStatus.OFFLINE);
        userRepository.save(fetchedEntity);
    }

    public void setUpGame(User userToBeAdded) {
        Optional<GameEntity> optionalGame = gameRepository.findById((long)1);

        /*
         if game already exists, simply add the user
         if game does not exist, create new game and add the user
         */
        optionalGame.ifPresent((game) -> {
            addUserToGame(userToBeAdded, game);
            // Check if there are already five players in the game
            if (game.getAllUsers().size() == 5) {
                try {
                    game.setup();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (optionalGame.isEmpty()){
            createGame(userToBeAdded);
        }
    }

    private void deleteUserFromGame(Long UserID, GameEntity gameEntity) {
        gameEntity.removeUserFromAll(UserID);
        gameEntity.removeUserFromActive(UserID);
    }

    /**
     * Helper Function
     * Temporary function to create a GameEntity and save it in the GameRepository
     * Since no Lobby is implemented in Milestone 3, a base game gets created as soon as a user registers/logs in
     *
     * @param firstUserInGame first user to join the game
     */
    private void createGame(User firstUserInGame) {
        GameEntity game = new GameEntity();

        game.addUserToAll(firstUserInGame);
        game.addUserToActive(firstUserInGame);

        //Set default gameName
        game.setGameName("default");

        gameRepository.save(game);
        gameRepository.flush();

    }

    /**
     * Helper Function
     * Used to add a registered/logged in user to a GameEntity
     *
     * @param userToBeAdded user that should be added to the game list
     */
    private void addUserToGame(User userToBeAdded, GameEntity gameEntity) {

        gameEntity.addUserToAll(userToBeAdded);
        gameEntity.addUserToActive(userToBeAdded);

        gameRepository.save(gameEntity);
        gameRepository.flush();
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided is not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username"));
        }
    }
}
