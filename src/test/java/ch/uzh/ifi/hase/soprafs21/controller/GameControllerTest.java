package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.OpponentInGameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GameController.class)
class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private LoginService loginService;

    private User testUser;

    /**
     * Test if gameData is fetched by a logged in user
     */

    /*
    @Test
    void getGameData_success() throws Exception {
        //given
        GameEntity gameEntity = new GameEntity();
        ArrayList<OpponentInGameGetDTO> opponents = new ArrayList<>();

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("test");
        user1.setToken("1");
        gameEntity.addUserToAll(user1);
        gameEntity.addUserToActive(user1);
        loginService.createUser(user1);

        User user2 = new User();
        user2.setUsername("user1");
        user2.setPassword("test");
        user2.setToken("2");
        gameEntity.addUserToAll(user2);
        gameEntity.addUserToActive(user2);
        loginService.createUser(user2);

        List<User> players = new ArrayList<>(gameEntity.getAllUsers());
        players.remove(user2);

        for (User player : players){
            opponents.add(DTOMapper.INSTANCE.convertEntityToOpponentInGameGetDTO(player));
        }

        gameEntity.setGameName("default");
        gameEntity.setOpponents(opponents);

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken(user2.getToken());

        // will return the gameEntity
        given(gameService.getGameData(1, user2)).willReturn(gameEntity);

        //when
        MockHttpServletRequestBuilder getRequest = get("/games/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        //then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameName", is("default")));
    } */
    /**
     * Test if game could not be found
     */
    @Test
    void getGameData_notFound(){

    }

    /**
     * Test if user is not authorized to get gameData
     */
    @Test
    void getGameData_unauthorized(){

    }

    @Test
    void userfolds_success() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername1");
        testUser.setToken("1");
        testUser.setMoney(10);
        testUser.setGamestatus(GameStatus.READY);

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("1");

        given(gameService.getUserByIdInActiveUsers(Mockito.any(), Mockito.any())).willReturn(testUser);

        MockHttpServletRequestBuilder putRequest = put("/games/1/1/fold")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            MvcResult result = mockMvc.perform(putRequest)
                    .andExpect(status().isNoContent())
                    .andReturn();

            assertEquals("", result.getResponse().getContentAsString());
        }
        catch (Exception e) {
            /**
             * Here, the Exception should not occur. If an Exception gets caught, the Test should fail.
             */
            fail();
        }
    }


    @Test
    void userfolds_notallowed() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("1");

        //given() doesn't work with a void function
        given(gameService.getUserByIdInActiveUsers(Mockito.any(), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED,"The User is not found... (In Fold process)"));


        MockHttpServletRequestBuilder putRequest = put("/games/1/1/fold")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isUnauthorized())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("401 UNAUTHORIZED \"The User is not found... (In Fold process)\"", result.getResolvedException().getMessage()))
                    ;
        }
        catch (Exception e) {
            /**
             * Here, the Exception should not occur. If an Exception gets caught, the Test should fail.*/

            fail();
        }
    }

    @Test
    void userraises_success(){
        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername1");
        testUser.setToken("1");
        testUser.setMoney(10);
        testUser.setGamestatus(GameStatus.READY);

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("1");
        userPutDTO.setRaiseamount(10);

        given(gameService.getUserByIdInActiveUsers(Mockito.any(), Mockito.any())).willReturn(testUser);

        MockHttpServletRequestBuilder putRequest = put("/games/1/1/raise")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO))
                ;

        try {
            MvcResult result = mockMvc.perform(putRequest)
                    .andExpect(status().isNoContent())
                    .andReturn();

            assertEquals("", result.getResponse().getContentAsString());
        }
        catch (Exception e) {
            /**
             * Here, the Exception should not occur. If an Exception gets caught, the Test should fail.
             */
            fail();
        }

    }

    @Test
    void userraises_notallowed(){
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("1");
        userPutDTO.setRaiseamount(10);

        //given() doesn't work with a void function
        given(gameService.getUserByIdInActiveUsers(Mockito.any(), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED,"The User is not found... (In Raise process)"));


        MockHttpServletRequestBuilder putRequest = put("/games/1/1/raise")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isUnauthorized())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("401 UNAUTHORIZED \"The User is not found... (In Raise process)\"", result.getResolvedException().getMessage()))
            ;
        }
        catch (Exception e) {
            /**
             * Here, the Exception should not occur. If an Exception gets caught, the Test should fail.*/

            fail();
        }}

    @Test
    void userraises_notenoughmoney(){
        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername1");
        testUser.setToken("1");
        testUser.setMoney(10);
        testUser.setGamestatus(GameStatus.READY);
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("1");
        userPutDTO.setRaiseamount(10);

        //given() doesn't work with a void function
        given(gameService.getUserByIdInActiveUsers(Mockito.any(), Mockito.any())).willReturn(testUser);
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "The User doesn't have enough money to raise with such an amount!")).when(gameService).userRaises(Mockito.any(),Mockito.any(),Mockito.anyInt());

        MockHttpServletRequestBuilder putRequest = put("/games/1/1/raise")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isConflict())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("409 CONFLICT \"The User doesn't have enough money to raise with such an amount!\"", result.getResolvedException().getMessage()))
            ;
        }
        catch (Exception e) {
            fail();
        }}

    @Test
    void usercalls_success(){
        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername1");
        testUser.setToken("1");
        testUser.setMoney(10);
        testUser.setGamestatus(GameStatus.READY);

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("1");
        userPutDTO.setRaiseamount(10);

        given(gameService.getUserByIdInActiveUsers(Mockito.any(), Mockito.any())).willReturn(testUser);

        MockHttpServletRequestBuilder putRequest = put("/games/1/1/call")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO))
                ;

        try {
            MvcResult result = mockMvc.perform(putRequest)
                    .andExpect(status().isNoContent())
                    .andReturn();

            assertEquals("", result.getResponse().getContentAsString());
        }
        catch (Exception e) {
            /**
             * Here, the Exception should not occur. If an Exception gets caught, the Test should fail.
             */
            fail();
        }
    }

    @Test
    void usercalls_notallowed(){
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("1");


        //given() doesn't work with a void function
        given(gameService.getUserByIdInActiveUsers(Mockito.any(), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED,"The User is not found... (In Call process)"));


        MockHttpServletRequestBuilder putRequest = put("/games/1/1/call")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isUnauthorized())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("401 UNAUTHORIZED \"The User is not found... (In Call process)\"", result.getResolvedException().getMessage()))
            ;
        }
        catch (Exception e) {
            /**
             * Here, the Exception should not occur. If an Exception gets caught, the Test should fail.*/

            fail();
        }

    }



    @Test
    void userchecks_success(){
        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername1");
        testUser.setToken("1");
        testUser.setMoney(10);
        testUser.setGamestatus(GameStatus.READY);

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("1");

        given(gameService.getUserByIdInActiveUsers(Mockito.any(), Mockito.any())).willReturn(testUser);

        MockHttpServletRequestBuilder putRequest = put("/games/1/1/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO))
                ;

        try {
            MvcResult result = mockMvc.perform(putRequest)
                    .andExpect(status().isNoContent())
                    .andReturn();

            assertEquals("", result.getResponse().getContentAsString());
        }
        catch (Exception e) {
            /**
             * Here, the Exception should not occur. If an Exception gets caught, the Test should fail.
             */
            fail();
        }

    }

    @Test
    void userchecks_notallowed(){
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("1");


        //given() doesn't work with a void function
        given(gameService.getUserByIdInActiveUsers(Mockito.any(), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED,"The User is not found... (In Check process)"));


        MockHttpServletRequestBuilder putRequest = put("/games/1/1/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isUnauthorized())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("401 UNAUTHORIZED \"The User is not found... (In Check process)\"", result.getResolvedException().getMessage()))
            ;
        }
        catch (Exception e) {
            /**
             * Here, the Exception should not occur. If an Exception gets caught, the Test should fail.*/

            fail();
        }

    }

    @Test
    void userchecks_butnotpossible(){
        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername1");
        testUser.setToken("1");
        testUser.setMoney(10);
        testUser.setGamestatus(GameStatus.READY);

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("1");


        //given() doesn't work with a void function
        given(gameService.getUserByIdInActiveUsers(Mockito.any(), Mockito.any())).willReturn(testUser);
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "This User cannot check, since a different User has a different amount of money in the pot!")).when(gameService).userChecks(Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/games/1/1/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isConflict())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("409 CONFLICT \"This User cannot check, since a different User has a different amount of money in the pot!\"", result.getResolvedException().getMessage()))
            ;
        }
        catch (Exception e) {
            fail();
        }}

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
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