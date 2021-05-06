package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs21.constant.MessageType;
import ch.uzh.ifi.hase.soprafs21.constant.Show;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.protocol.ProtocolElement;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.service.ChatService;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    User testUser;

    @Test
    void userChats_success() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setMessage("I want to win!");
        userPutDTO.setToken("123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername1");
        testUser.setToken("123");
        testUser.setMoney(10);
        testUser.setGamestatus(GameStatus.READY);
        testUser.setWantsToShow(Show.NOT_DECIDED);

        given(chatService.getUserInGameById(Mockito.any(), Mockito.any())).willReturn(testUser);

        MockHttpServletRequestBuilder putRequest = put("/games/1/1/chats")
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
    void userWantsToChat_butIsNotAuthorized() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setMessage("I want to win!");
        userPutDTO.setToken("456");

        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername1");
        testUser.setToken("123");
        testUser.setMoney(10);
        testUser.setGamestatus(GameStatus.READY);
        testUser.setWantsToShow(Show.NOT_DECIDED);

        given(chatService.getUserInGameById(Mockito.any(), Mockito.any())).willReturn(testUser);

        MockHttpServletRequestBuilder putRequest = put("/games/1/1/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isUnauthorized())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("401 UNAUTHORIZED \"User not authorized to send a chat message\"", result.getResolvedException().getMessage()))
            ;


        }
        catch (Exception e) {
            /**
             * Here, the Exception should not occur. If an Exception gets caught, the Test should fail.
             */
            fail();
        }

    }

    @Test
    void userWantsToChat_butWasNotFound() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setMessage("I want to win!");
        userPutDTO.setToken("456");

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "The User could not be found...")).when(chatService).getUserInGameById(Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/games/1/1/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));
        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("404 NOT_FOUND \"The User could not be found...\"", result.getResolvedException().getMessage()))
            ;


        }
        catch (Exception e) {
            /**
             * Here, the Exception should not occur. If an Exception gets caught, the Test should fail.
             */
            fail();
        }

    }

    @Test
    void getLog_success() {
        ArrayList<ProtocolElement> protocols = new ArrayList<ProtocolElement>();

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("test");
        user1.setToken("1");

        String token = "123";

        GameEntity game = new GameEntity();

        ProtocolElement p1 = new ProtocolElement(MessageType.CHAT, user1, "I want to win!!");
        protocols.add(p1);
        ProtocolElement p2 = new ProtocolElement(MessageType.LOG, game, "One more card is dealt.");
        protocols.add(p2);

        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername1");
        testUser.setToken(token);
        testUser.setMoney(10);
        testUser.setGamestatus(GameStatus.READY);
        testUser.setWantsToShow(Show.NOT_DECIDED);

        given(chatService.getUserInGameById(Mockito.any(), Mockito.any())).willReturn(testUser);
        given(chatService.getProtocol(Mockito.any())).willReturn(protocols);

        MockHttpServletRequestBuilder getRequest = get("/games/1/1/chats")
                .header("Authorization", token);
        try {
            mockMvc.perform(getRequest).andExpect(status().isOk())
                    .andExpect(jsonPath("$.[0]messageType", is(MessageType.CHAT.toString())))
                    .andExpect(jsonPath("$.[0]name", is(user1.getName())))
                    .andExpect(jsonPath("$.[0]message", is("I want to win!!")))
                    .andExpect(jsonPath("$.[1]messageType", is(MessageType.LOG.toString())))
                    .andExpect(jsonPath("$.[1]name", is(game.getName())))
                    .andExpect(jsonPath("$.[1]message", is("One more card is dealt.")))
            ;
        }
        catch (Exception e) {
            /**
             * Here, the Exception should not occur. If an Exception gets caught, the Test should fail.
             */
            fail();
        }
    }

    @Test
    void getLog_notAuthorized() {
        String token = "123";

        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername1");
        testUser.setToken(token);
        testUser.setMoney(10);
        testUser.setGamestatus(GameStatus.READY);
        testUser.setWantsToShow(Show.NOT_DECIDED);

        given(chatService.getUserInGameById(Mockito.any(), Mockito.any())).willReturn(testUser);

        MockHttpServletRequestBuilder getRequest = get("/games/1/1/chats")
                .header("Authorization", "456");

        try {
            mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void getLog_userWasNotFound() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "The User could not be found...")).when(chatService).getUserInGameById(Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder getRequest = get("/games/1/1/chats")
                .header("Authorization", "456");

        try {
            mockMvc.perform(getRequest).andExpect(status().isNotFound());
        }
        catch (Exception e) {
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