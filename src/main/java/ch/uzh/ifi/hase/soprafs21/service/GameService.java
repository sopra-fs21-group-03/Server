package ch.uzh.ifi.hase.soprafs21.service;


import ch.uzh.ifi.hase.soprafs21.constant.Blind;
import ch.uzh.ifi.hase.soprafs21.constant.MessageType;
import ch.uzh.ifi.hase.soprafs21.constant.Round;
import ch.uzh.ifi.hase.soprafs21.constant.Show;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.protocol.ProtocolElement;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.OpponentInGameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.PlayerInGameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.timer.CentralScheduler;
import ch.uzh.ifi.hase.soprafs21.timer.tasks.PotDistributor;
import ch.uzh.ifi.hase.soprafs21.timer.tasks.SkipUserIfAFK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * GameService
 * This class is the "worker" and responsible for a lot of functionality related to the Game and the actions that a User can perform during a Game session
 * (e.g., a User can fold, raise, call or check). If there is a result, it will be passed back to the caller.
 */
@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    private static final String NOT_FOUND_MESSAGE = "The User could not be found...";
    private static final String NOT_IN_TURN_MESSAGE = "This User is not in turn!";


    private static final int AMOUNT_OF_GAMES = 4;

    // Turn time in ms
    private static final long TURN_TIME = 30000L;

    // Time left at the end of showdown
    private static final long SHOWDOWN_TIME = 4000L;

    /**
     * @param gameRepository this is the Repository which the GameService will receive. Since the GameService is responsible for actions related to saved
     *                       games, we need a Repository that saves Games.
     */
    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("userRepository") UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        createGames();
    }

    private void createGames() {
        for (Long i = 1L; i <= AMOUNT_OF_GAMES; i++) {
            var game = new GameEntity(i);
            gameRepository.save(game);
        }
        gameRepository.flush();
    }

    public long getTurnTime() {
        return TURN_TIME;
    }

    /**
     * @param gameid The id of the Game that should be analyzed
     * @param user   The User who wants to perform an actions. It needs to be checked, if he is on turn
     * @return true if the User that called this method is on turn. Else, return false
     */
    private boolean checkIfUserPerformingActionIsUserOnTurn(Long gameid, User user) {
        var theGame = findGameEntity(gameid);
        return theGame.getOnTurn().getUsername().equals(user.getUsername());
    }

    /**
     * @param gameid The id of the Game that should be analyzed
     * @return the GameEntity with the corresponding gameid, if it exists. Else, if there is no game with such an id, a ResponseStatusException will be thrown.
     */
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


    /**
     * @param theGame The id of the Game that should be analyzed
     * @param userid  The id of the User that should be returned. Here, we are searching inside the allUsers List.
     * @return The User if there is a User with the id userid in allUsers. Else, if there is no User with such an id, a ResponseStatusException will be thrown
     */
    public User getUserByIdInAllUsers(GameEntity theGame, Long userid) {
        for (User user : theGame.getAllUsers()) {
            if (userid.equals(user.getId())) {
                return user;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE);
    }

    public User getUserByIdInAllUsersAndSpectators(GameEntity theGame, Long userid) {
        for (User user : theGame.getAllUsers()) {
            if (userid.equals(user.getId())) {
                return user;
            }
        }
        for (User user2 : theGame.getSpectators()) {
            if (userid.equals(user2.getId())) {
                return user2;
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE);
    }


    /**
     * @param theGame The Game that should be analyzed
     * @param userid  The id of the User that should be returned. Here, we are searching inside the activeUsers List.
     * @return The User if there is a User with the id userid in activeUsers. Else, if there is no User with such an id, a ResponseStatusException will be thrown
     */
    public User getUserByIdInActiveUsers(GameEntity theGame, Long userid) {
        for (User user : theGame.getActiveUsers()) {
            if (userid.equals(user.getId())) {
                return user;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE);
    }


    private void saveFlushUserRepoForUsersInAllUsersAndSpectators(GameEntity theGame) {
        for (User user2 : theGame.getAllUsers()) {
            userRepository.saveAndFlush(user2);
        }
        for (User user3 : theGame.getSpectators()) {
            userRepository.saveAndFlush(user3);
        }
    }

    /**
     * @param theGame The id of the Game that should be analyzed
     * @param userid  The id of the User that wants to perform the "Fold" action.
     */
    public void userFolds(GameEntity theGame, Long userid) {
        startTurnTimerForNextUser(theGame.getId());
        //then, find the User with the id userid. For performing an action, a User has to be in the activeUsers List.
        var user = getUserByIdInActiveUsers(theGame, userid);

        //is this User on turn?
        if (checkIfUserPerformingActionIsUserOnTurn(theGame.getId(), user)) {

            // give me the Username of the potential next User in turn. In userFolds(), this method called
            // getUsernameOfPotentialNextUserInTurn() is crucial, since when performing a fold, we are going to delete
            // the User that called this userFolds() method from the activeUsers-List. But when we do this, we cannot find this User
            // anymore in the activeUsers List and find who is the potential next User in turn. Therefore, before removing this current User
            // from the activeUsers List, we need to know how is potentially the next User in turn.

            var element = new ProtocolElement(MessageType.LOG, theGame, String.format("User %s folds", user.getUsername()));
            theGame.addProtocolElement(element);
            String usernameOfPotentialNextUserInTurn = theGame.getUsernameOfPotentialNextUserInTurn(user);
            //User folds -> he gets removed from the ActiveUsers List, not from the AllUsers List
            theGame.getActiveUsers().remove(user);
            gameRepository.saveAndFlush(theGame);
            //then, set the next User on turn or the next round or declare a winner.
            theGame.roundHandler(usernameOfPotentialNextUserInTurn);

            saveFlushUserRepoForUsersInAllUsersAndSpectators(theGame);
            gameRepository.saveAndFlush(theGame);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, NOT_IN_TURN_MESSAGE);
        }
    }

    /**
     * @param theGame The id of the Game that should be analyzed
     * @param userid  The id of the User that wants to perform the "Fold" action.
     * @param amount  The User wants to raise by this amount.
     */
    public void userRaises(GameEntity theGame, Long userid, int amount) {
        startTurnTimerForNextUser(theGame.getId());
        // You cannot raise by 0 or a negative number.
        if (amount <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The raise amount always has to be above 0!");
        }

        //then, find the User with the id userid. For performing an action, a User has to be in the activeUsers List.
        var user = getUserByIdInActiveUsers(theGame, userid);
        //is this User on turn?
        if (checkIfUserPerformingActionIsUserOnTurn(theGame.getId(), user)) {
            //User is not the User that raised last
            if (theGame.getUserThatRaisedLast() == null || !theGame.getUserThatRaisedLast().getId().equals(user.getId())) {
                // The "normal" case: the User has more money than the raise amount.
                if (user.getMoney() > amount) {
                    //The amount should be removed from the User's money.
                    user.removeMoney(amount);
                    //put the money inside the pot
                    raiseHandler(theGame, user, amount);
                }
                else if (user.getMoney() == amount) {
                    /**
                     This is the All-In Case
                     */
                    // All-In -> After raising, the User doesn't have money anymore.
                    user.setMoney(0);
                    //put the money inside the pot
                    raiseHandler(theGame, user, amount);
                }
                else {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "The User doesn't have enough money to raise with such an amount!");
                }

            }
            else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This User was the User that raised last! Therefore, he cannot raise a second time in a row!");
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, NOT_IN_TURN_MESSAGE);
        }
    }

    private void raiseHandler(GameEntity theGame, User user, int amount) {
        theGame.getPot().addMoney(user, amount);
        //create log message
        var element = new ProtocolElement(MessageType.LOG, theGame, String.format("User %s raised by %d. %s has %d in the pot", user.getUsername(), amount, user.getUsername(), theGame.getPot().getUserContributionOfAUser(user)));
        theGame.addProtocolElement(element);
        //the User calling this method is the new User that raised last
        theGame.setUserThatRaisedLast(user);
        //This was not a check-action -> therefore, the counter, will be put to 0
        theGame.setCheckcounter(0);
        //Give me the username of the User that is potentially the next user on turn
        String usernameOfPotentialNextUserInTurn = theGame.getUsernameOfPotentialNextUserInTurn(user);
        //then, set the next User on turn or the next round or declare a winner.
        theGame.roundHandler(usernameOfPotentialNextUserInTurn);
        theGame.setBigblindspecialcase(false);
        saveFlushUserRepoForUsersInAllUsersAndSpectators(theGame);
        gameRepository.saveAndFlush(theGame);
    }

    /**
     * @param theGame -> Id of the GameSession
     * @param userid  -> Id of User that wants to call
     *                <p>
     *                In this function, "All-In" will also be handeled
     */
    public void userCalls(GameEntity theGame, Long userid) {
        startTurnTimerForNextUser(theGame.getId());
        //then: give me the player that raised last
        var lastRaiser = theGame.getUserThatRaisedLast();
        //If you are not in the PREFLOP round and noone raised -> calling is like checking
        if (lastRaiser == null && theGame.getRound() != Round.PREFLOP) {
            // userChecks will be called, since noone called before. ATTENTION: in the first round, where we have the
            // input of BIG and SMALL Blind, this function should not be called
            userChecks(theGame, userid);
            return;
        }

        //In the function call, we got a userid. Give me this User
        var thisUser = getUserByIdInActiveUsers(theGame, userid);
        //is this User on turn?
        if (checkIfUserPerformingActionIsUserOnTurn(theGame.getId(), thisUser)) {
            //if someone wants to call -> he wants to have the same amount of money in the pot as the user that raised last
            int totalPotContributionOfPlayerThatRaisedLast = theGame.getPot().getUserContributionOfAUser(lastRaiser);
            // amount this User already has in the pot
            var amountThisUserAlreadyHasInThePot = theGame.getPot().getUserContributionOfAUser(thisUser);
            //This is the "normal" call process. The User has enough money
            if (thisUser.getMoney() + amountThisUserAlreadyHasInThePot >= totalPotContributionOfPlayerThatRaisedLast) {
                int difference = totalPotContributionOfPlayerThatRaisedLast - amountThisUserAlreadyHasInThePot;
                thisUser.removeMoney(difference);
                theGame.getPot().addMoney(thisUser, difference);
                // log
                var element = new ProtocolElement(MessageType.LOG, theGame, String.format("User %s called. %s has %d in the pot", thisUser.getUsername(), thisUser.getUsername(), theGame.getPot().getUserContributionOfAUser(thisUser)));
                theGame.addProtocolElement(element);
            }
            else {
                /**
                 * This is the All-In Case
                 */
                theGame.getPot().addMoney(thisUser, thisUser.getMoney());
                thisUser.setMoney(0);
                // log
                var element = new ProtocolElement(MessageType.LOG, theGame, String.format("User %s went all in. %s has %d in the pot", thisUser.getUsername(), thisUser.getUsername(), theGame.getPot().getUserContributionOfAUser(thisUser)));
                theGame.addProtocolElement(element);
            }
            if (theGame.isBigblindspecialcase() && theGame.getRound() == Round.PREFLOP) {
                theGame.setUserThatRaisedLast(thisUser);
                theGame.setBigblindspecialcase(false);
            }

            //This was not a check-action -> therefore, the counter, will be put to 0
            theGame.setCheckcounter(0);
            //Give me the username of the User that is potentially the next user on turn
            String usernameOfPotentialNextUserInTurn = theGame.getUsernameOfPotentialNextUserInTurn(thisUser);
            //then, set the next User on turn or the next round or declare a winner.
            theGame.roundHandler(usernameOfPotentialNextUserInTurn);
            saveFlushUserRepoForUsersInAllUsersAndSpectators(theGame);
            gameRepository.saveAndFlush(theGame);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, NOT_IN_TURN_MESSAGE);
        }
    }

    /**
     * @param theGame -> Id of the GameSession
     * @param userid  -> Id of User that wants to call
     *                <p>
     *                When a User is raising, before he can raise, he needs to call.
     */
    public void userCallsForRaising(GameEntity theGame, Long userid) {
        startTurnTimerForNextUser(theGame.getId());

        //give me the player that raise last
        var lastRaiser = theGame.getUserThatRaisedLast();
        if (lastRaiser == null) {
            return;
        }

        //In the function call, we got a userid. Give me this User
        var thisUser = getUserByIdInActiveUsers(theGame, userid);
        //is this User on turn?
        if (checkIfUserPerformingActionIsUserOnTurn(theGame.getId(), thisUser)) {
            //if someone wants to call -> he wants to have the same amount of money in the pot as the user that raised last
            int totalPotContributionOfPlayerThatRaisedLast = theGame.getPot().getUserContributionOfAUser(lastRaiser);
            var amountThisUserAlreadyHasInThePot = theGame.getPot().getUserContributionOfAUser(thisUser);

            if (thisUser.getMoney() > 0 && (thisUser.getMoney() + amountThisUserAlreadyHasInThePot >= totalPotContributionOfPlayerThatRaisedLast)) {
                int difference = totalPotContributionOfPlayerThatRaisedLast - amountThisUserAlreadyHasInThePot;
                thisUser.removeMoney(difference);
                theGame.getPot().addMoney(thisUser, difference);

            }
            else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The User cannot raise, since he has not enough money!");
            }
            theGame.setCheckcounter(0);
            saveFlushUserRepoForUsersInAllUsersAndSpectators(theGame);
            gameRepository.saveAndFlush(theGame);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, NOT_IN_TURN_MESSAGE);
        }
    }


    /**
     * @param theGame The id of the Game that should be analyzed
     * @param userid  The id of the User that wants to perform the "Check" action.
     */
    public void userChecks(GameEntity theGame, Long userid) {
        startTurnTimerForNextUser(theGame.getId());

        //In the function call, we got a userid. Give me this User
        var thisUser = getUserByIdInActiveUsers(theGame, userid);
        //is this User on turn?
        if (checkIfUserPerformingActionIsUserOnTurn(theGame.getId(), thisUser)) {
            //If a User wants to check -> no one else should have more Contribution in the Pot than he has. For this, the method loops in activeUsers
            for (User user : theGame.getActiveUsers()) {
                if (theGame.getPot().getUserContributionOfAUser(user) > theGame.getPot().getUserContributionOfAUser(thisUser)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "This User cannot check, since a different User has a different amount of money in the pot!");
                }
            }
            // log
            var element = new ProtocolElement(MessageType.LOG, theGame, String.format("User %s checked", thisUser.getUsername()));
            theGame.addProtocolElement(element);
            //Checking happened -> increase the counter
            theGame.setCheckcounter(theGame.getCheckcounter() + 1);
            //Give me the username of the User that is potentially the next user on turn
            String usernameOfPotentialNextUserInTurn = theGame.getUsernameOfPotentialNextUserInTurn(thisUser);
            //then, set the next User on turn or the next round or declare a winner.
            theGame.roundHandler(usernameOfPotentialNextUserInTurn);
            saveFlushUserRepoForUsersInAllUsersAndSpectators(theGame);
            gameRepository.saveAndFlush(theGame);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, NOT_IN_TURN_MESSAGE);
        }
    }

    /**
     * Gets the gameData for a game
     *
     * @param gameID              ID of the game we would like to fetch
     * @param userWhoWantsToFetch needs a token set
     * @return GameEntity of the requested game
     */
    public GameEntity getGameData(long gameID, User userWhoWantsToFetch) {
        Optional<GameEntity> optionalGame = gameRepository.findById(gameID);
        var valid = false;
        ArrayList<OpponentInGameGetDTO> opponents = new ArrayList<>();

        if (optionalGame.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The game you requested was not found");
        }

        GameEntity game = optionalGame.get();

        for (User user : game.getRawPlayersInTurnOrder()) {
            if (user.getToken().equals(userWhoWantsToFetch.getToken())) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not allowed to get data about this game, since he was not found in the Session!");
        }

        for (User player : game.getRawPlayersInTurnOrder()) {
            var opponent = DTOMapper.INSTANCE.convertEntityToOpponentInGameGetDTO(player);

            // Set inGame status of a user to true if he's still in the game, to false if he has left
            opponent.setInGame(game.getAllUsers().contains(player) || game.getSpectators().contains(player));
            opponent.setFolded(game.getAllUsers().contains(player) && !game.getActiveUsers().contains(player));
            opponents.add(opponent);
        }

        game.setPlayersInTurnOrder(opponents);

        return game;
    }

    /**
     * Used to get own gameData during game(i.e own cards)
     *
     * @param gameID              ID of the requested Game
     * @param userID              ID of the user requesting the data
     * @param userWhoWantsToFetch needs a token set
     * @return User Entity of the requested data
     */
    public PlayerInGameGetDTO getOwnGameData(Long gameID, Long userID, User userWhoWantsToFetch) {
        Optional<GameEntity> optionalGame = gameRepository.findById(gameID);
        var valid = false;

        if (optionalGame.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The game you requested was not found");
        }

        GameEntity game = optionalGame.get();
        User player = null;

        List<User> players = new ArrayList<>(game.getAllUsers());
        players.addAll(game.getSpectators());

        for (User user : players) {
            if (user.getId().equals(userID)) {
                player = user;
                if (player.getToken().equals(userWhoWantsToFetch.getToken())) {
                    valid = true;
                }
                break;
            }
        }

        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }

        if (!valid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Player not logged in");
        }

        var playerReturn = DTOMapper.INSTANCE.convertEntityToPlayerInGameGetDTO(player);

        playerReturn.setFolded(!game.getActiveUsers().contains(player) && game.getAllUsers().contains(player));

        return playerReturn;
    }

    /**
     * Used to get the data during the showdown round of a poker game
     *
     * @param gameID              ID of the requested game
     * @param userWhoWantsToFetch token needs to be set, used for authentication
     * @return Lists of users in game as PlayerInGameGetDTOs
     */
    public List<PlayerInGameGetDTO> getDataDuringShowdown(Long gameID, User userWhoWantsToFetch) {
        var game = findGameEntity(gameID);
        // Copy all users to a new list
        List<User> rawPlayers = new ArrayList<>(game.getRawPlayersInTurnOrder());

        // Copy them as DTOs so when modifying them original objects are unchanged
        List<PlayerInGameGetDTO> playerInGameGetDTOS = new ArrayList<>();

        var valid = false;

        //Check if game is already
        if (!game.getShowdown()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Showdown round hasn't started yet.");
        }

        for (User player : rawPlayers) {
            if (player.getToken().equals(userWhoWantsToFetch.getToken())) {
                valid = true;
            }

            var placeHolder = DTOMapper.INSTANCE.convertEntityToPlayerInGameGetDTO(player);

            // If the player doesn't want to show its cards replace it with an empty list
            if (!player.getWantsToShow().equals(Show.SHOW)) {
                // Set cards to an empty list
                placeHolder.setCards(new ArrayList<>());
            }
            playerInGameGetDTOS.add(placeHolder);
        }

        if (!valid)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to get this data");

        return playerInGameGetDTOS;
    }

    public void show(GameEntity game, User user, boolean wantsToShow) {
        startTurnTimerForNextUser(game.getId());

        if (checkIfUserPerformingActionIsUserOnTurn(game.getId(), user)) {
            Show show;
            if (wantsToShow) {
                show = userDoesWantsToShow(game, user);
            }
            else {
                show = userDoesNotWantToShow(game, user);
            }
            user.setWantsToShow(show);

            //check if all active users decided whether to show or not
            for (User activeUser : game.getActiveUsers()) {
                if (activeUser.getWantsToShow() == Show.NOT_DECIDED) {
                    return;
                }
            }

            //if all user decided distribute the pot
            // Set on Turn to null when all have decided
            game.setOnTurn(null);
            startShowdownTimerForLastUser(game);
        }
        else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, NOT_IN_TURN_MESSAGE);
        }
    }

    private Show userDoesWantsToShow(GameEntity game, User user) {
        var show = Show.SHOW;
        try {
            game.nextTurnInShowdown(user);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, NOT_IN_TURN_MESSAGE);
        }
        return show;
    }

    private Show userDoesNotWantToShow(GameEntity game, User user) {
        Show show;
        show = Show.DONT_SHOW;
        try {
            game.nextTurnInShowdown(user);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, NOT_FOUND_MESSAGE);

        }
        game.removeUserFromActive(user.getId());
        return show;
    }

    public GameEntity getGameById(Long gameId) {
        Optional<GameEntity> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game not found");
        }
        else {
            return game.get();
        }
    }

    public void deleteUserFromGame(Long userID, GameEntity gameEntity, User realUser) {

        // End screen case
        if (gameEntity.getRound() == Round.ENDED) {
            gameEntity.removeUserFromAll(userID);
            gameEntity.removeUserFromActive(userID);
            gameEntity.removeUserFromSpectators(userID);
            gameEntity.removeUserFromRawPlayers(userID);

            if (!gameEntity.isFirstGameSetup()) {
                gameEntity.setFirstGameSetup(true);
                gameEntity.setProtocol(new ArrayList<>());
            }
        }
        else { // Game must still be running
            /*
            Copy the user that left into the rawPlayersInTurn list,
            the user will be skipped by the server but a copy will still be returned to the client to not break anything
             */

            int idx = gameEntity.getAllUsers().indexOf(realUser);

            if (realUser.getBlind() == Blind.BIG) {

                realUser.setBlind(Blind.NEUTRAL);
                List<User> allUsers = gameEntity.getAllUsers();
                var toChange = allUsers.get(Math.abs((idx - 1 + allUsers.size()) % (allUsers.size())));
                toChange.setBlind(Blind.BIG);

            }

            if (realUser.getBlind() == Blind.SMALL) {
                realUser.setBlind(Blind.NEUTRAL);
                List<User> allUsers = gameEntity.getAllUsers();
                var toChange = allUsers.get(Math.abs((idx + 1) % (allUsers.size())));
                toChange.setBlind(Blind.SMALL);
            }

            if (gameEntity.getOnTurn().getUsername().equals(realUser.getUsername()) && !(gameEntity.getActiveUsers().size() == 1)) {
                var username = gameEntity.getUsernameOfPotentialNextUserInTurn(realUser);
                gameEntity.roundHandler(username);
            }

            gameEntity.removeUserFromAll(userID);
            gameEntity.removeUserFromActive(userID);
            gameEntity.removeUserFromSpectators(userID);

            if (gameEntity.getActiveUsers().size() == 1){
                gameEntity.redistributePot();
                gameEntity.setRound(Round.ENDED);
            }

            var protocol = new ProtocolElement(MessageType.LOG, realUser, String.format("User %s has left the table", realUser.getUsername()));
            gameEntity.addProtocolElement(protocol);

        }

        realUser.setGameId(null);
        userRepository.saveAndFlush(realUser);
        saveFlushUserRepoForUsersInAllUsersAndSpectators(gameEntity);
        gameRepository.saveAndFlush(gameEntity);
    }


    /**
     * Helper functions for threading
     */

    public void startShowdownTimerForLastUser(GameEntity game) {
        var potDistributor = new PotDistributor(game, this.gameRepository, this.userRepository, this);
        CentralScheduler.getInstance().reset(potDistributor, SHOWDOWN_TIME);
    }


    public void startTurnTimerForNextUser(long gameID) {
        var skipUserIfAFK = new SkipUserIfAFK(this.gameRepository, this, gameID);

        CentralScheduler.getInstance().reset(skipUserIfAFK, TURN_TIME);
    }

    public void startTurnTimer(long gameID) {
        var skipUserIfAFK = new SkipUserIfAFK(this.gameRepository, this, gameID);
        CentralScheduler.getInstance().start(skipUserIfAFK, TURN_TIME);
    }
}
