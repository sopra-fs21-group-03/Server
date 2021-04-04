package ch.uzh.ifi.hase.soprafs21.controller;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        User folderuserfound = gameService.getUserById(gameid, userid);
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
        User raiserUserFound = gameService.getUserById(gameid, userid);
        if (raiserUserInput.getToken().equals(raiserUserFound.getToken())){
            gameService.userRaises(gameid, userid, raiseamount);
        }
        else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The User is not authorized... (In Raise process)");
        }


    }







}