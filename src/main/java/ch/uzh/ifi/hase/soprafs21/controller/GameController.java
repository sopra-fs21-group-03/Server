package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class GameController {

    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Put Mapping to update a User and a GameEntity according to the User's action. Here, it is a fold
     * Code:
     * - 204 if the fold was successful. Nothing will be returned
     * - 404 if gameData or the User was not found
     * - 401 if User is not authorized to perform this action or if he is not in turn
     *
     * @param gameid     get the gameID of the requested game
     * @param userid     the id of the user who wants to perform this action
     * @param userPutDTO a Object which containts the token of the user
     */
    @PutMapping("/games/{GameID}/{UserID}/fold")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void userfolds(@PathVariable("GameID") Long gameid, @PathVariable("UserID") Long userid, @RequestBody UserPutDTO userPutDTO) {
        var entity = gameService.findGameEntity(gameid);
        var folderuserinput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        var folderuserfound = gameService.getUserByIdInActiveUsers(entity, userid);
        if (folderuserfound.getToken().equals(folderuserinput.getToken())) {
            gameService.userFolds(entity, userid);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The User is not authorized... (In Fold process)");
        }
    }

    /**
     * Put Mapping to update a User and a GameEntity according to the User's action. Here, it is a raise
     * Code:
     * - 204 if the raise was successful. Nothing will be returned
     * - 404 if gameData was not found or the User was not found
     * - 401 if User is not authorized to perform this action or if he is not in turn
     * - 409 if User does not have enough money or if generally a money problem occurs (e.g. raiseamount is below 0)
     *
     * @param gameid     get the gameID of the requested game
     * @param userid     the id of the user who wants to perform this action
     * @param userPutDTO a Object which containts the token of the user and the raiseamount
     */
    @PutMapping("/games/{GameID}/{UserID}/raise")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void userraises(@PathVariable("GameID") Long gameid, @PathVariable("UserID") Long userid, @RequestBody UserPutDTO userPutDTO) {

        int raiseamount = userPutDTO.getRaiseAmount();
        var raiserUserInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        var entity = gameService.findGameEntity(gameid);
        var raiserUserFound = gameService.getUserByIdInActiveUsers(entity, userid);
        if (raiserUserInput.getToken().equals(raiserUserFound.getToken())) {
            if (raiseamount > 0) {
                gameService.userCallsForRaising(entity, userid);
                gameService.userRaises(entity, userid, raiseamount);
            }
            else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The raise amount always has to be above 0!");
            }

        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The User is not authorized... (In Raise process)");
        }


    }

    /**
     * Put Mapping to update a User and a GameEntity according to the User's action. Here, it is a call
     * Code:
     * - 204 if the call was successful. Nothing will be returned
     * - 404 if gameData was not found or the User was not found
     * - 401 if User is not authorized to perform this action or if he is not in turn
     * - 409 if User does not have any money or if generally a money problem occurs
     *
     * @param gameid     get the gameID of the requested game
     * @param userid     the id of the user who wants to perform this action
     * @param userPutDTO a Object which containts the token of the user
     */
    @PutMapping("/games/{GameID}/{UserID}/call")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void usercalls(@PathVariable("GameID") Long gameid, @PathVariable("UserID") Long userid, @RequestBody UserPutDTO userPutDTO) {
        var entity = gameService.findGameEntity(gameid);
        var callerUserInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        var callerUserFound = gameService.getUserByIdInActiveUsers(entity, userid);
        if (callerUserInput.getToken().equals(callerUserFound.getToken())) {
            gameService.userCalls(entity, userid);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The User is not authorized... (In Call process)");
        }
    }

    /**
     * Put Mapping to update a User and a GameEntity according to the User's action. Here, it is a check
     * Code:
     * - 204 if the check was successful. Nothing will be returned
     * - 404 if gameData was not found or the User was not found
     * - 401 if User is not authorized to perform this action or if he is not in turn
     * - 409 if checking is not allowed, since some one else has more money in the pot than this User.
     *
     * @param gameid     get the gameID of the requested game
     * @param userid     the id of the user who wants to perform this action
     * @param userPutDTO a Object which containts the token of the user
     */
    @PutMapping("/games/{GameID}/{UserID}/check")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void userchecks(@PathVariable("GameID") Long gameid, @PathVariable("UserID") Long userid, @RequestBody UserPutDTO userPutDTO) {
        var entity = gameService.findGameEntity(gameid);
        var checkerUserInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        var checkerUserFound = gameService.getUserByIdInActiveUsers(entity, userid);
        if (checkerUserInput.getToken().equals(checkerUserFound.getToken())) {
            gameService.userChecks(entity, userid);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The User is not authorized... (In Call process)");
        }
    }

    /**
     * Get Mapping to get the game Data
     * Code: - 200 if gameData could be fetched
     * - 404 if gameData was not found
     * - 401 if User is not authenticated (SHOULD BE WRITTEN INTO THE REST SPECIFICATION!)
     *
     * @param gameId get the gameID of the requested game
     * @param token  get the token
     * @return gameData and List of opponents
     */
    @GetMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGameData(@PathVariable Long gameId, @RequestHeader(value = "Authorization") String token) {
        var userWhoWantsToFetch = new User();
        userWhoWantsToFetch.setToken(token);

        GameEntity game = gameService.getGameData(gameId, userWhoWantsToFetch);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);

    }

    /**
     * Get Mapping to get own gameData
     * Code: - 200 if own gameData could be fetched
     * - 404 if own gameData was not found
     * - 401 if the gameData does not belong to the user requesting it
     *
     * @param gameId Id of the game
     * @param userId Id of the user
     * @param token  used to get the token of the user
     * @return own gameData
     */
    @GetMapping("/games/{gameId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerInGameGetDTO getOwnGameData(@PathVariable Long gameId, @PathVariable Long userId, @RequestHeader(value = "Authorization") String token) {
        var userWhoWantsToFetch = new User();
        userWhoWantsToFetch.setToken(token);

        User player = gameService.getOwnGameData(gameId, userId, userWhoWantsToFetch);

        return DTOMapper.INSTANCE.convertEntityToPlayerInGameGetDTO(player);
    }

    /**
     * Used to get information during the showdown
     * -200 if information is fetched
     * -401 if user is not authorized to get this information
     * -409 if game is not yet in showdown round
     * -404 if game could not be found
     *
     * @param gameId ID of the requested game
     * @param token  Token of user requesting the information
     * @return List of all users that want to show their cards
     */
    @GetMapping("/games/{gameId}/showdown")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerInGameGetDTO> getGameDataDuringShowdown(@PathVariable Long gameId, @RequestHeader(value = "Authorization") String token) {
        var userWhoWantsToFetch = new User();
        userWhoWantsToFetch.setToken(token);

        return gameService.getDataDuringShowdown(gameId, userWhoWantsToFetch);
    }

    /**
     * Request used to decide whether or not to show ones cards in a showdown
     * Sets the requesting users field wantsToShow according to the sent boolean
     * Code: - 204 if successful
     * - 401 if the user or game could not be found
     * - 404 if the user requesting is not authorized to change the state of the requested user
     *
     * @param gameId         used to search the game
     * @param userId         used to search the user
     * @param userShowPutDTO used to get the token(Authentication) and a boolean deciding whether to show or not
     */
    @PutMapping("/games/{gameId}/{userId}/show")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void showCards(@PathVariable Long gameId, @PathVariable Long userId, @RequestBody UserShowPutDTO userShowPutDTO) {
        var entity = gameService.findGameEntity(gameId);
        //search for user, finds only if user is in the game as active user
        var user = gameService.getUserByIdInActiveUsers(entity, userId);

        //verify token
        if (!user.getToken().equals(userShowPutDTO.getToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to decide");
        }

        GameEntity game = gameService.getGameById(gameId);
        gameService.show(game, user, userShowPutDTO.isWantsToShow());
    }

    /**
     * Put Mapping used to leave a Game
     * Code:
     * - 204 if leave was successful
     * - 401 if wrong token is sent by client
     * - 404 if user is logged in but not present in the gameSession he wants to leave
     *
     * @param gameId     id of the game the user wants to leave
     * @param userId     the id of the user who wants to leave
     * @param userPutDTO DTO containing the users authentication token
     */
    @PutMapping("/games/{gameId}/{userId}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void userLeavesGameSession(@PathVariable Long gameId, @PathVariable Long userId, @RequestBody UserPutDTO userPutDTO) {
        var entity = gameService.findGameEntity(gameId);
        var realUser = gameService.getUserByIdInAllUsersAndSpectators(entity, userId);
        var userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

        // Check token
        if (userInput.getToken().equals(realUser.getToken())){
            gameService.deleteUserFromGame(userId, entity, realUser);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong token for user");
        }

    }


}