package ch.uzh.ifi.hase.soprafs21.service;


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

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

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

    public User getUserByIdInAllUsers(Long gameid, Long userid) {
        GameEntity theGame = findGameEntity(gameid);

        for (User user : theGame.getAllUsers()) {
            if (userid.equals(user.getId())) {
                return user;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The User could not be found...");
    }

    public User getUserByIdInActiveUsers(Long gameid, Long userid) {
        GameEntity theGame = findGameEntity(gameid);

        for (User user : theGame.getActiveUsers()) {
            if (userid.equals(user.getId())) {
                return user;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The User could not be found...");
    }


    public void userFolds(Long gameid, Long userid) {
        /**
         * What if a User is a Big/Small Blind? In the first round in the first turn, they should not be able to fold, but afterwards they still can fold!
         */
        GameEntity theGame = findGameEntity(gameid);
        for (User user : theGame.getActiveUsers()) {
            if (userid.equals(user.getId())) {
                theGame.getActiveUsers().remove(user);
                gameRepository.save(theGame);
                return;
                /**
                 *  User folds -> he gets removed from the ActiveUsers List, not from the AllUsers List
                 */
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The User could not be found...");

    }


    public void userRaises(Long gameid, Long userid, int amount) {
        if(amount < 0){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The raise amount always has to be above 0!");
        }
        GameEntity theGame = findGameEntity(gameid);

        for (User user : theGame.getActiveUsers()) {
            if (userid.equals(user.getId())) {
                if (theGame.getUserThatRaisedLast() == null || !theGame.getUserThatRaisedLast().getId().equals(user.getId())) {
                /*
                User was found and he is not the User that raised last
                 */
                    if (user.getMoney() > amount) {
                        theGame.getPot().addMoney(user, amount);
                        theGame.setUserThatRaisedLast(user);
                        try {
                            user.removeMoney(amount);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                        gameRepository.save(theGame);
                        return;
                    }
                    else if (user.getMoney() == amount) {
                        /**
                         This is the All-In Case
                         */
                        theGame.getPot().addMoney(user, amount);
                        theGame.setUserThatRaisedLast(user);
                        user.setMoney(0);
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
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The User could not be found...");

    }

    /**
     * @param gameid -> Id of the GameSession
     * @param userid -> Id of User that wants to call
     *
     *               In this function, "All-In" will also be handeled
     *
     *
     */
    public void userCalls(Long gameid, Long userid) {
        GameEntity theGame = findGameEntity(gameid);

        //first: give me the player that raise last
        User lastRaiser = theGame.getUserThatRaisedLast();
        //In the function call, we got a userid. Give me this User
        User thisUser = getUserByIdInActiveUsers(gameid, userid);

        if(thisUser.getMoney() <= 0){
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
            /**
             * This is the All-In Case
             */
            theGame.getPot().addMoney(thisUser, thisUser.getMoney());
            thisUser.setMoney(0);
        }

        gameRepository.save(theGame);
    }
}
