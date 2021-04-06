package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GameController.class)
class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    private User testUser;

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