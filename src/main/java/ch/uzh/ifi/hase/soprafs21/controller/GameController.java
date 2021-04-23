package ch.uzh.ifi.hase.soprafs21.controller;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;;

@RestController
public class GameController {

    private final GameService gameService;

    GameController(GameService gameService){this.gameService = gameService;}

    /**
     *
     * Put Mapping to update a User and a GameEntity according to the User's action. Here, it is a fold
     * Code:
     * - 204 if the fold was successful. Nothing will be returned
     * - 404 if gameData or the User was not found
     * - 401 if User is not authorized to perform this action or if he is not in turn
     *
     *
     * @param gameid get the gameID of the requested game
     * @param userid the id of the user who wants to perform this action
     * @param userPutDTO a Object which containts the token of the user
     */
    @PutMapping("/games/{GameID}/{UserID}/fold")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void userfolds(@PathVariable("GameID") Long gameid, @PathVariable("UserID") Long userid, @RequestBody UserPutDTO userPutDTO){
        User folderuserinput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        User folderuserfound = gameService.getUserByIdInActiveUsers(gameid, userid);
        if (folderuserfound.getToken().equals(folderuserinput.getToken())){
            gameService.userFolds(gameid, userid);
        }
        else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The User is not authorized... (In Fold process)");
        }
    }

    /**
     *
     * Put Mapping to update a User and a GameEntity according to the User's action. Here, it is a raise
     * Code:
     * - 204 if the raise was successful. Nothing will be returned
     * - 404 if gameData was not found or the User was not found
     * - 401 if User is not authorized to perform this action or if he is not in turn
     * - 409 if User does not have enough money or if generally a money problem occurs (e.g. raiseamount is below 0)
     *
     * @param gameid get the gameID of the requested game
     * @param userid the id of the user who wants to perform this action
     * @param userPutDTO a Object which containts the token of the user and the raiseamount
     */
    @PutMapping("/games/{GameID}/{UserID}/raise")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void userraises(@PathVariable("GameID") Long gameid, @PathVariable("UserID") Long userid, @RequestBody UserPutDTO userPutDTO){
        int raiseamount = userPutDTO.getRaiseAmount();
        User raiserUserInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        User raiserUserFound = gameService.getUserByIdInActiveUsers(gameid, userid);
        if (raiserUserInput.getToken().equals(raiserUserFound.getToken())){
            if(raiseamount>0){
                gameService.userCallsForRaising(gameid, userid);
                gameService.userRaises(gameid, userid, raiseamount);
            } else{
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The raise amount always has to be above 0!");
            }

        }
        else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The User is not authorized... (In Raise process)");
        }
        /**
         * Something is missing: if a User raises, the "Call-amount" for the next player changes. But how does the Front-End know about this new Call-Amount, which
         * should be displayed on the Call-Button for the next player?
         */


    }

    /**
     *
     * Put Mapping to update a User and a GameEntity according to the User's action. Here, it is a call
     * Code:
     * - 204 if the call was successful. Nothing will be returned
     * - 404 if gameData was not found or the User was not found
     * - 401 if User is not authorized to perform this action or if he is not in turn
     * - 409 if User does not have any money or if generally a money problem occurs
     *
     * @param gameid get the gameID of the requested game
     * @param userid the id of the user who wants to perform this action
     * @param userPutDTO a Object which containts the token of the user
     */
    @PutMapping("/games/{GameID}/{UserID}/call")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void usercalls(@PathVariable("GameID") Long gameid, @PathVariable("UserID") Long userid, @RequestBody UserPutDTO userPutDTO){
        User callerUserInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        User callerUserFound = gameService.getUserByIdInActiveUsers(gameid, userid);
        if (callerUserInput.getToken().equals(callerUserFound.getToken())){
            gameService.userCalls(gameid, userid);
        }
        else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The User is not authorized... (In Call process)");
        }
    }

    /**
     *
     * Put Mapping to update a User and a GameEntity according to the User's action. Here, it is a check
     * Code:
     * - 204 if the check was successful. Nothing will be returned
     * - 404 if gameData was not found or the User was not found
     * - 401 if User is not authorized to perform this action or if he is not in turn
     * - 409 if checking is not allowed, since some one else has more money in the pot than this User.
     *
     * @param gameid get the gameID of the requested game
     * @param userid the id of the user who wants to perform this action
     * @param userPutDTO a Object which containts the token of the user
     */
    @PutMapping("/games/{GameID}/{UserID}/check")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void userchecks(@PathVariable("GameID") Long gameid, @PathVariable("UserID") Long userid, @RequestBody UserPutDTO userPutDTO){
        User checkerUserInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        User checkerUserFound = gameService.getUserByIdInActiveUsers(gameid, userid);
        if (checkerUserInput.getToken().equals(checkerUserFound.getToken())){
            gameService.userChecks(gameid, userid);
        }
        else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The User is not authorized... (In Call process)");
        }
    }

    /**
     * Get Mapping to get the game Data
     * Code: - 200 if gameData could be fetched
     *       - 404 if gameData was not found
     *       - 401 if User is not authenticated (SHOULD BE WRITTEN INTO THE REST SPECIFICATION!)
     * @param GameID get the gameID of the requested game
     * @param token get the token
     * @return gameData and List of opponents
     */
    @GetMapping("/games/{GameID}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGameData(@PathVariable Long GameID, @RequestHeader(value = "Authorization") String token){
        User userWhoWantsToFetch = new User();
        userWhoWantsToFetch.setToken(token);

        GameEntity game = gameService.getGameData(GameID, userWhoWantsToFetch);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);

    }

    /**
     * Get Mapping to get own gameData
     * Code: - 200 if own gameData could be fetched
     *       - 404 if own gameData was not found
     *       - 401 if the gameData does not belong to the user requesting it
     * @param GameID Id of the game
     * @param UserID Id of the user
     * @param token used to get the token of the user
     * @return own gameData
     */
    @GetMapping("/games/{GameID}/{UserID}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerInGameGetDTO getOwnGameData(@PathVariable Long GameID, @PathVariable Long UserID, @RequestHeader(value = "Authorization") String token){
        User userWhoWantsToFetch = new User();
        userWhoWantsToFetch.setToken(token);

        User player = gameService.getOwnGameData(GameID, UserID, userWhoWantsToFetch);

        return DTOMapper.INSTANCE.convertEntityToPlayerInGameGetDTO(player);
    }

    /**
     * Used to get information during the showdown
     *  -200 if information is fetched
     *  -401 if user is not authorized to get this information
     *  -409 if game is not yet in showdown round
     *  -404 if game could not be found
     * @param GameID ID of the requested game
     * @param token Token of user requesting the information
     * @return List of all users that want to show their cards
     */
    @GetMapping("/games/{GameID}/showdown")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerInGameGetDTO> getGameDataDuringShowdown(@PathVariable Long GameID, @RequestHeader(value="Authorization") String token){
        User userWhoWantsToFetch = new User();
        userWhoWantsToFetch.setToken(token);

        List<PlayerInGameGetDTO> playersDuringShowdown = gameService.getDataDuringShowdown(GameID, userWhoWantsToFetch);


        return playersDuringShowdown;
    }

    /**
     * Request used to decide whether or not to show ones cards in a showdown
     * Sets the requesting users field wantsToShow according to the sent boolean
     * Code: - 204 if successful
     *       - 401 if the user or game could not be found
     *       - 404 if the user requesting is not authorized to change the state of the requested user
     * @param gameID used to search the game
     * @param userID used to search the user
     * @param userShowPutDTO used to get the token(Authentication) and a boolean deciding whether to show or not
     */
    @PutMapping("/games/{gameID}/{userID}/show")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void showCards(@PathVariable Long gameID, @PathVariable Long userID, @RequestBody UserShowPutDTO userShowPutDTO) {
        //search for user, finds only if user is in the game as active user
        User user = gameService.getUserByIdInActiveUsers(gameID, userID);

        //verify token
        if(!user.getToken().equals(userShowPutDTO.getToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to decide");
        }

        GameEntity game = gameService.getGameById(gameID);
        gameService.show(game, user, userShowPutDTO.isWantsToShow());
    }
}