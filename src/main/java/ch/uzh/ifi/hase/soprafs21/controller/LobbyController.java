package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.SpecificLobbyGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LobbyController {

    private final LobbyService lobbyService;


    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @GetMapping("/lobbies")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LobbyGetDTO> getLobbies(@RequestHeader(value = "Authorization") String token) {
        lobbyService.checkIfUserExists_ByToken(token);
        var games = lobbyService.getAllGames();
        List<LobbyGetDTO> lobbies = new ArrayList<>();
        for (GameEntity game : games) {
            lobbies.add(DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(game));
        }
        return lobbies;
    }

    @PutMapping("/lobbies/{lobbyID}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void addUserToLobby(@PathVariable Long lobbyID, @RequestBody UserPutDTO userPutDTO) {
        lobbyService.checkIfUserExists_ByToken(userPutDTO.getToken());
        User userfound = lobbyService.getUserByTokenInUserRepository(userPutDTO.getToken());
        GameEntity entity = lobbyService.findGameEntity(lobbyID);
        /*
        Only add the User to the Game if there is space and if the session has not started yet.
         */
        lobbyService.addUserToGame(userfound, entity);
    }

    @PutMapping("/lobbies/{lobbyID}/{userID}/ready")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void setReadyStatus(@PathVariable Long lobbyID, @PathVariable Long userID, @RequestBody UserPutDTO userPutDTO) {
        GameEntity entity = lobbyService.findGameEntity(lobbyID);
        User userfound = lobbyService.getUserInSpecificGameSessionInAllUsers(userID, entity);

        /*
        You should only be able set your NOT-READY Status to READY. This means, that I need to set the Status back to NOT-READY, once
        the Session has ended.
         */

        lobbyService.setUserToReady(userfound);
    }

    @GetMapping("/lobbies/{lobbyID}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SpecificLobbyGetDTO getSpecificLobbyInformation(@PathVariable Long lobbyID, @RequestHeader(value = "Authorization") String token) {
        lobbyService.checkIfUserExists_ByToken(token);
        lobbyService.checkIfUserIsInGameSession(token, lobbyID);
        GameEntity game = lobbyService.getSpecificLobbyData(lobbyID);
        return DTOMapper.INSTANCE.convertEntityToSpecificLobbyGetDTO(game);
    }
}
