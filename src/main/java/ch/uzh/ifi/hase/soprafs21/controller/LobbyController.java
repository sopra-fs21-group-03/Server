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
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LobbyController {

    private final LobbyService lobbyService;
    private static final String NOT_FOUND_MESSAGE = "User is not registered or logged in";


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
        lobbyService.checkIfUserIsAlreadyInAnOtherLobby(userPutDTO.getToken(),lobbyID);
        var userfound = lobbyService.getUserByTokenInUserRepository(userPutDTO.getToken());
        var entity = lobbyService.findGameEntity(lobbyID);
        /*
        Only add the User to the Game if there is space and if the session has not started yet.
         */
        lobbyService.addUserToGame(userfound, entity);
    }

    @PutMapping("/lobbies/{lobbyID}/{userID}/ready")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void setReadyStatus(@PathVariable Long lobbyID, @PathVariable Long userID, @RequestBody UserPutDTO userPutDTO) {
        var entity = lobbyService.findGameEntity(lobbyID);
        var userfound = lobbyService.getUserInSpecificGameSessionInAllUsers(userID, entity);


        if (!userfound.getToken().equals(userPutDTO.getToken())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, NOT_FOUND_MESSAGE);
        }

        /*
        You should only be able set your NOT-READY Status to READY. This means, that I need to set the Status back to NOT-READY, once
        the Session has ended.
         */

        lobbyService.setUserToReady(userfound);
        lobbyService.setUpGame(entity);
    }

    @PutMapping("/lobbies/{lobbyID}/{userID}/unready")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void setUnreadyStatus(@PathVariable Long lobbyID, @PathVariable Long userID, @RequestBody UserPutDTO userPutDTO){
        var entity = lobbyService.findGameEntity(lobbyID);
        var userFound = lobbyService.getUserInSpecificGameSessionInAllUsers(userID, entity);

        if (!userFound.getToken().equals(userPutDTO.getToken())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, NOT_FOUND_MESSAGE);
        }

        lobbyService.setUserToUnready(userFound);
    }

    @PutMapping("/lobbies/{lobbyID}/{userID}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveLobby(@PathVariable Long lobbyID, @PathVariable Long userID, @RequestBody UserPutDTO userPutDTO){
        var entity = lobbyService.findGameEntity(lobbyID);
        var userFound = lobbyService.getUserInSpecificGameSessionInAllUsers(userID, entity);

        if (!userFound.getToken().equals(userPutDTO.getToken())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, NOT_FOUND_MESSAGE);
        }

        lobbyService.leaveLobby(userFound, entity);
    }


    @GetMapping("/lobbies/{lobbyID}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SpecificLobbyGetDTO getSpecificLobbyInformation(@PathVariable Long lobbyID, @RequestHeader(value = "Authorization") String token) {
        lobbyService.checkIfUserExists_ByToken(token);
        lobbyService.checkIfUserIsInGameSession(token, lobbyID);
        var game = lobbyService.getSpecificLobbyData(lobbyID);
        return DTOMapper.INSTANCE.convertEntityToSpecificLobbyGetDTO(game);
    }
}
