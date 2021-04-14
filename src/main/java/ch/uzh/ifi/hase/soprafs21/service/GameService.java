package ch.uzh.ifi.hase.soprafs21.service;


import ch.uzh.ifi.hase.soprafs21.constant.Round;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.OpponentInGameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
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

    /**
     * @param gameRepository this is the Repository which the GameService will receive. Since the GameService is responsible for actions related to saved
     *                       games, we need a Repository that saves Games.
     */
    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /**
     * @param gameid The id of the Game that should be analyzed
     * @param user   The User who wants to perform an actions. It needs to be checked, if he is on turn
     * @return true if the User that called this method is on turn. Else, return false
     */
    private boolean checkIfUserPerformingActionIsUserOnTurn(Long gameid, User user) {
        GameEntity theGame = findGameEntity(gameid);
        return theGame.getOnTurn().getUsername().equals(user.getUsername());
    }

    /**
     * @param gameid The id of the Game that should be analyzed
     * @return the GameEntity with the corresponding gameid, if it exists. Else, if there is no game with such an id, a ResponseStatusException will be thrown.
     */
    private GameEntity findGameEntity(Long gameid) {
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
     * @param gameid The id of the Game that should be analyzed
     * @param userid The id of the User that should be returned. Here, we are searching inside the allUsers List.
     * @return The User if there is a User with the id userid in allUsers. Else, if there is no User with such an id, a ResponseStatusException will be thrown
     */
    public User getUserByIdInAllUsers(Long gameid, Long userid) {
        GameEntity theGame = findGameEntity(gameid);

        for (User user : theGame.getAllUsers()) {
            if (userid.equals(user.getId())) {
                return user;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The User could not be found...");
    }

    /**
     * @param gameid The id of the Game that should be analyzed
     * @param userid The id of the User that should be returned. Here, we are searching inside the activeUsers List.
     * @return The User if there is a User with the id userid in activeUsers. Else, if there is no User with such an id, a ResponseStatusException will be thrown
     */
    public User getUserByIdInActiveUsers(Long gameid, Long userid) {
        GameEntity theGame = findGameEntity(gameid);

        for (User user : theGame.getActiveUsers()) {
            if (userid.equals(user.getId())) {
                return user;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The User could not be found...");
    }


    /**
     * @param gameid The id of the Game that should be analyzed
     * @param userid The id of the User that wants to perform the "Fold" action.
     */
    public void userFolds(Long gameid, Long userid) {
        //first, find the GameEntity (find it with the id called gameid)
        GameEntity theGame = findGameEntity(gameid);
        //then, find the User with the id userid. For performing an action, a User has to be in the activeUsers List.
        for (User user : theGame.getActiveUsers()) {
            //You found the User (.equals() method)
            if (userid.equals(user.getId())) {
                //is this User on turn?
                if (checkIfUserPerformingActionIsUserOnTurn(gameid, user)) {

                    // give me the Username of the potential next User in turn. In userFolds(), this method called
                    // getUsernameOfPotentialNextUserInTurn() is crucial, since when performing a fold, we are going to delete
                    // the User that called this userFolds() method from the activeUsers-List. But when we do this, we cannot find this User
                    // anymore in the activeUsers List and find who is the potential next User in turn. Therefore, before removing this current User
                    // from the activeUsers List, we need to know how is potentially the next User in turn.

                    String usernameOfPotentialNextUserInTurn = theGame.getUsernameOfPotentialNextUserInTurn(user);
                    //User folds -> he gets removed from the ActiveUsers List, not from the AllUsers List
                    theGame.getActiveUsers().remove(user);
                    //then, set the next User on turn or the next round or declare a winner.
                    theGame.setNextUserOrNextRoundOrSomeoneHasAlreadyWon(usernameOfPotentialNextUserInTurn);
                    gameRepository.save(theGame);
                    return;

                }
                else {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This User is not in turn!");
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The User could not be found...");

    }

    /**
     * @param gameid The id of the Game that should be analyzed
     * @param userid The id of the User that wants to perform the "Fold" action.
     * @param amount The User wants to raise by this amount.
     */
    public void userRaises(Long gameid, Long userid, int amount) {
        // You cannot raise by 0 or a negative number.
        if (amount <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The raise amount always has to be above 0!");
        }
        //first, find the GameEntity (find it with the id called gameid)
        GameEntity theGame = findGameEntity(gameid);

        //then, find the User with the id userid. For performing an action, a User has to be in the activeUsers List.
        for (User user : theGame.getActiveUsers()) {
            //You found the User (.equals() method)
            if (userid.equals(user.getId())) {
                //is this User on turn?
                if (checkIfUserPerformingActionIsUserOnTurn(gameid, user)) {
                    //User is not the User that raised last
                    if (theGame.getUserThatRaisedLast() == null || !theGame.getUserThatRaisedLast().getId().equals(user.getId())) {
                        // The "normal" case: the User has more money than the raise amount.
                        if (user.getMoney() > amount) {
                            //The amount should be removed from the User's money.
                            try {
                                user.removeMoney(amount);
                            }
                            catch (Exception e) {
                                throw new ResponseStatusException(HttpStatus.CONFLICT, "Something went wrong when trying the remove the raise amount from the User's money!");
                            }
                            //put the money inside the pot
                            theGame.getPot().addMoney(user, amount);
                            //the User calling this method is the new User that raised last
                            theGame.setUserThatRaisedLast(user);
                            //This was not a check-action -> therefore, the counter, will be put to 0
                            theGame.setCheckcounter(0);
                            //Give me the username of the User that is potentially the next user on turn
                            String usernameOfPotentialNextUserInTurn = theGame.getUsernameOfPotentialNextUserInTurn(user);
                            //then, set the next User on turn or the next round or declare a winner.
                            theGame.setNextUserOrNextRoundOrSomeoneHasAlreadyWon(usernameOfPotentialNextUserInTurn);
                            gameRepository.save(theGame);
                            return;
                        }
                        else if (user.getMoney() == amount) {
                            /**
                             This is the All-In Case
                             */

                            // All-In -> After raising, the User doesn't have money anymore.
                            user.setMoney(0);
                            //put the money inside the pot
                            theGame.getPot().addMoney(user, amount);
                            //the User calling this method is the new User that raised last
                            theGame.setUserThatRaisedLast(user);
                            //This was not a check-action -> therefore, the counter, will be put to 0
                            theGame.setCheckcounter(0);
                            //Give me the username of the User that is potentially the next user on turn
                            String usernameOfPotentialNextUserInTurn = theGame.getUsernameOfPotentialNextUserInTurn(user);
                            //then, set the next User on turn or the next round or declare a winner.
                            theGame.setNextUserOrNextRoundOrSomeoneHasAlreadyWon(usernameOfPotentialNextUserInTurn);
                            gameRepository.save(theGame);
                            return;

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
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This User is not in turn!");
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The User could not be found...");

    }

    /**
     * @param gameid -> Id of the GameSession
     * @param userid -> Id of User that wants to call
     *
     *               In this function, "All-In" will also be handeled
     */
    public void userCalls(Long gameid, Long userid) {
        //first, find the GameEntity (find it with the id called gameid)
        GameEntity theGame = findGameEntity(gameid);
        //then: give me the player that raised last
        User lastRaiser = theGame.getUserThatRaisedLast();
        //If you are not in the PREFLOP round and noone raised -> calling is like checking
        if (lastRaiser == null && theGame.getRound() != Round.PREFLOP) {
            // userChecks will be called, since noone called before. ATTENTION: in the first round, where we have the
            // input of BIG and SMALL Blind, this function should not be called
            userChecks(gameid, userid);
        }

        //In the function call, we got a userid. Give me this User
        User thisUser = getUserByIdInActiveUsers(gameid, userid);
        //is this User on turn?
        if (checkIfUserPerformingActionIsUserOnTurn(gameid, thisUser)) {
            //If a User has no money, he should not be able to make a turn again
            if (thisUser.getMoney() <= 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The User cannot call, since he has no money!");
            }

            //if someone wants to call -> he wants to have the same amount of money in the pot as the user that raised last
            int totalPotContributionOfPlayerThatRaisedLast = theGame.getPot().getUserContributionOfAUser(lastRaiser);
            // amount this User already has in the pot
            int amountThisUserAlreadyHasInThePot = theGame.getPot().getUserContributionOfAUser(thisUser);
            //This is the "normal" call process. The User has enough money
            if (thisUser.getMoney() + amountThisUserAlreadyHasInThePot >= totalPotContributionOfPlayerThatRaisedLast) {
                int difference = totalPotContributionOfPlayerThatRaisedLast - amountThisUserAlreadyHasInThePot;

                try {
                    thisUser.removeMoney(difference);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                theGame.getPot().addMoney(thisUser, difference);

            }
            else {
                /**
                 * This is the All-In Case
                 */
                theGame.getPot().addMoney(thisUser, thisUser.getMoney());
                thisUser.setMoney(0);
            }
            //This was not a check-action -> therefore, the counter, will be put to 0
            theGame.setCheckcounter(0);
            //Give me the username of the User that is potentially the next user on turn
            String usernameOfPotentialNextUserInTurn = theGame.getUsernameOfPotentialNextUserInTurn(thisUser);
            //then, set the next User on turn or the next round or declare a winner.
            theGame.setNextUserOrNextRoundOrSomeoneHasAlreadyWon(usernameOfPotentialNextUserInTurn);
            gameRepository.save(theGame);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This User is not in turn!");
        }
    }

    /**
     *
     * @param gameid -> Id of the GameSession
     * @param userid -> Id of User that wants to call
     *
     *               When a User is raising, before he can raise, he needs to call.
     */
    public void userCallsForRaising(Long gameid, Long userid) {
        GameEntity theGame = findGameEntity(gameid);

        //give me the player that raise last
        User lastRaiser = theGame.getUserThatRaisedLast();
        if(lastRaiser == null){
            return;
        }

        //In the function call, we got a userid. Give me this User
        User thisUser = getUserByIdInActiveUsers(gameid, userid);
        //is this User on turn?
        if (checkIfUserPerformingActionIsUserOnTurn(gameid, thisUser)) {

            if (thisUser.getMoney() <= 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The User cannot call, since he has no money!");
            }

            //if someone wants to call -> he wants to have the same amount of money in the pot as the user that raised last
            int totalPotContributionOfPlayerThatRaisedLast = theGame.getPot().getUserContributionOfAUser(lastRaiser);
            int amountThisUserAlreadyHasInThePot = theGame.getPot().getUserContributionOfAUser(thisUser);

            if (thisUser.getMoney() + amountThisUserAlreadyHasInThePot >= totalPotContributionOfPlayerThatRaisedLast) {
                int difference = totalPotContributionOfPlayerThatRaisedLast - amountThisUserAlreadyHasInThePot;

                try {
                    thisUser.removeMoney(difference);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                theGame.getPot().addMoney(thisUser, difference);

            }
            else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The User cannot raise, since he has not enough money!");
            }
            theGame.setCheckcounter(0);
            gameRepository.save(theGame);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This User is not in turn!");
        }
    }

    /**
     * @param gameid The id of the Game that should be analyzed
     * @param userid The id of the User that wants to perform the "Check" action.
     *
     */
    public void userChecks(Long gameid, Long userid) {
        GameEntity theGame = findGameEntity(gameid);

        //In the function call, we got a userid. Give me this User
        User thisUser = getUserByIdInActiveUsers(gameid, userid);
        //is this User on turn?
        if (checkIfUserPerformingActionIsUserOnTurn(gameid, thisUser)) {
            //If a User wants to check -> no one else should have more Contribution in the Pot than he has. For this, the method loops in activeUsers
            for (User user : theGame.getActiveUsers()) {
                if (theGame.getPot().getUserContributionOfAUser(user) > theGame.getPot().getUserContributionOfAUser(thisUser)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "This User cannot check, since a different User has a different amount of money in the pot!");
                }
            }
            //Checking happened -> increase the counter
            theGame.setCheckcounter(theGame.getCheckcounter() + 1);
            //Give me the username of the User that is potentially the next user on turn
            String usernameOfPotentialNextUserInTurn = theGame.getUsernameOfPotentialNextUserInTurn(thisUser);
            //then, set the next User on turn or the next round or declare a winner.
            theGame.setNextUserOrNextRoundOrSomeoneHasAlreadyWon(usernameOfPotentialNextUserInTurn);
            gameRepository.save(theGame);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This User is not in turn!");
        }
    }

    /**
     * Gets the gameData for a game
     *
     * @param gameID
     * @param userWhoWantsToFetch
     * @return
     */
    public GameEntity getGameData(long gameID, User userWhoWantsToFetch) {
        Optional<GameEntity> optionalGame = gameRepository.findById(gameID);
        boolean valid = false;
        ArrayList<OpponentInGameGetDTO> opponents = new ArrayList<>();

        if (optionalGame.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The game you requested was not found");
        }

        GameEntity game = optionalGame.get();

        for (User user : game.getAllUsers()) {
            if (user.getToken().equals(userWhoWantsToFetch.getToken())) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        for (User player : game.getAllUsers()) {
            opponents.add(DTOMapper.INSTANCE.convertEntityToOpponentInGameGetDTO(player));
        }

        game.setPlayersInTurnOrder(opponents);

        return game;
    }

    public User getOwnGameData(Long gameID, Long userID, User userWhoWantsToFetch) {
        Optional<GameEntity> optionalGame = gameRepository.findById(gameID);
        boolean valid = false;

        if (optionalGame.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The game you requested was not found");
        }

        GameEntity game = optionalGame.get();
        User player = null;

        List<User> players = new ArrayList<>(game.getAllUsers());
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

        return player;
    }
}
