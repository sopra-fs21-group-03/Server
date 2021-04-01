package ch.uzh.ifi.hase.soprafs21.controller;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
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
     */
    @PutMapping("/games/{UserID}/fold")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void userfolds(@PathVariable("UserID") Long userid, @RequestBody UserPutDTO userPutDTO){
        User folderuserinput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        User folderuserfound = gameService.getUserById(userid);
        if (folderuserfound.getToken().equals(folderuserinput.getToken())){
            gameService.userFolds(folderuserfound.getId());
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The User could not be found... (In Fold process)");
        }



    }





}
