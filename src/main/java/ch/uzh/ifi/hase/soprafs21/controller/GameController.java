package ch.uzh.ifi.hase.soprafs21.controller;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameController {
    private final GameService gameService;

    GameController(GameService gameService){this.gameService = gameService;}

    @PutMapping("/games/{UserID}/fold")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void userfolds(@PathVariable("UserID") Long id, @RequestBody UserPutDTO userPutDTO){


    }





}
