package ch.uzh.ifi.hase.soprafs21.controller;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.PlayerInGameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

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
     * - 409 if User does not have enough money or if generally a money problem occurs
     *
     * @param gameid get the gameID of the requested game
     * @param userid the id of the user who wants to perform this action
     * @param userPutDTO a Object which containts the token of the user and the raiseamount
     */
    @PutMapping("/games/{GameID}/{UserID}/raise")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void userraises(@PathVariable("GameID") Long gameid, @PathVariable("UserID") Long userid, @RequestBody UserPutDTO userPutDTO){
        int raiseamount = userPutDTO.getRaiseamount();
        User raiserUserInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        User raiserUserFound = gameService.getUserByIdInActiveUsers(gameid, userid);
        if (raiserUserInput.getToken().equals(raiserUserFound.getToken())){
            gameService.userCallsForRaising(gameid, userid);
            gameService.userRaises(gameid, userid, raiseamount);
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
}