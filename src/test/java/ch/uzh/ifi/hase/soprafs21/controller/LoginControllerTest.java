package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.service.LoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    /* Test for getting all users (GET-Mapping) */
    @Test
    void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setPassword("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should return when getUsers() is called
        given(loginService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
    }

    /* Tests for registering a user (POST-Mapping) */
    @Test
    void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("Test User");
        userPostDTO.setUsername("testUsername");

        given(loginService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", is(user.getToken())));
    }

    @Test
    void createUser_invalidInput_errorThrown() throws Exception{
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        given(loginService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("TestPassword");
        userPostDTO.setUsername("testUsername");

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());
    }

    /* Tests for logging in a user (PUT-Mapping) */
    @Test
    void loginUser_validInput_userLoggedIn() throws Exception{
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("TestPassword");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.OFFLINE);

        given(loginService.checkLoginCredentials(Mockito.any())).willReturn(user);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("TestPassword");
        userPostDTO.setUsername("testUsername");

        MockHttpServletRequestBuilder putRequest = put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(user.getToken())));

    }

    @Test
    void loginUser_invalidInput_errorThrown() throws Exception{
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("TestPassword");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.OFFLINE);

        given(loginService.checkLoginCredentials(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("TestPassword1");
        userPostDTO.setUsername("testUsername");

        MockHttpServletRequestBuilder putRequest = put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(putRequest).andExpect(status().isUnauthorized());
    }

    /* Tests for logging out a User (PUT-Mapping)*/

    @Test
    void logoutUser_validInput_userLoggedOut() throws Exception{
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("TestPassword");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.OFFLINE);

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("1");

        MockHttpServletRequestBuilder putRequest = put("/users/" + user.getId() + "/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isNoContent());
    }

    @Test
    void logOutUser_invalidInput_notFound() throws Exception{

        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("TestPassword");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.OFFLINE);


        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("1");

        long falseID = user.getId()+1;

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(loginService).getUserToLogout(Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/users/" + falseID + "/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isNotFound());
    }

    @Test
    void logOutUser_invalidInput_unauthorized() throws Exception{


        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("TestPassword");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.OFFLINE);


        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("2");

        long falseID = user.getId()+1;

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED)).when(loginService).getUserToLogout(Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/users/" + falseID + "/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isUnauthorized());

    }


    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}