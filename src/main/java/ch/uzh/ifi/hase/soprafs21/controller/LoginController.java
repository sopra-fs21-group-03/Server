package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Login Controller
 * This class is responsible for handling all REST request that are related to the registration/login/logout.
 * The controller will receive the request and delegate the execution to the LoginService and finally return the result.
 */
@RestController
public class LoginController {

    private final LoginService loginService;

    LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * template mapping, nice to have for debugging
     * May get removed later on if not needed
     * @return List of all users stored in the userRepository
     */
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = loginService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    /**
     * This Mapping is used to register a user.
     * Throws a "Conflict" exception if username is already taken
     * @param userPostDTO Data of the user that wants to register
     * @return String containing the token of the registered user
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Map<String, String> createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        User createdUser = loginService.createUser(userInput);

        // Return String
        return Collections.singletonMap("token", createdUser.getToken());
    }

    /**
     * This mapping is used to login a user, if successful his token gets returned
     * and he is set to online.
     * @param userPostDTO Data of the user that wants to login
     * @return String containing the token of the registered user
     */
    @PutMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, String> loginUser(@RequestBody UserPostDTO userPostDTO){
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // If successful userStatus is set to online
        String token = loginService.checkLoginCredentials(userInput);

        /*
           TO DO
           Create Game and add User to the GameList
         */


        return Collections.singletonMap("token", token);
    }

    /**
     * This mapping is used to logout a user, if successful NO_CONTENT gets returned as status code
     * If unsuccessful either NOT_FOUND or UNAUTHORIZED gets returned
     * @param userPutDTO The token of the user who wants to logout
     * @param userID The ID of the user who wants to logout
     */
    @PutMapping("/users/{userID}/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logoutUser(@RequestBody UserPutDTO userPutDTO, @PathVariable(value="userID") Long userID){
        User toLogout = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

        loginService.getUserToLogout(toLogout, userID);


    }

}