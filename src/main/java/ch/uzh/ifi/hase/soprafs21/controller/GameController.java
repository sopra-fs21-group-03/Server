package ch.uzh.ifi.hase.soprafs21.controller;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
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
     * This is a Put Method for Fold. For the other actions, the beginnings of the methods will be similar to
     * this one
     * @param userid
     * @param userPutDTO
    **/

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The User could not be found... (In Fold process)");
        }
    }

    @PutMapping("/games/{GameID}/{UserID}/raise")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void userraises(@PathVariable("GameID") Long gameid, @PathVariable("UserID") Long userid, @RequestBody UserPutDTO userPutDTO){
        int raiseamount = userPutDTO.getRaiseamount();
        User raiserUserInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        User raiserUserFound = gameService.getUserByIdInActiveUsers(gameid, userid);
        if (raiserUserInput.getToken().equals(raiserUserFound.getToken())){
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
     * @param userPutDTO get the token
     * @return gameData and List of opponents
     */

    @GetMapping("/games/{GameID}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGameData(@PathVariable Long GameID, @RequestBody UserPutDTO userPutDTO){
        User userWhoWantsToFetch = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

        GameEntity game = gameService.getGameData(GameID, userWhoWantsToFetch);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);

    }
}