package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs21.constant.Show;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.PlayerInLobbyGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.service.LobbyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LobbyController.class)
class LobbyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;

    private GameEntity game;
    private GameEntity game2;
    private static final String NOT_FOUND_MESSAGE = "The User could not be found...";
    private static final String NOT_FOUND_MESSAGE_FOR_GAME = "The GameSession could not be found...";
    private User testUser;

    @BeforeEach
    void setup() {
        var user = new User();
        user.setUsername("1");
        var user2 = new User();
        user2.setUsername("2");
        var user3 = new User();
        user3.setUsername("3");
        var user4 = new User();
        user4.setUsername("4");
        var user5 = new User();
        user5.setUsername("5");

        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(List.of(
                user,
                user2,
                user3,
                user4,
                user5
        ));

        game = new GameEntity(1L);
        game.setId(1L);
        game.setGameName("1");
        game.setAllUsers(allUsers);

        var user6 = new User();
        user6.setUsername("6");
        user6.setGamestatus(GameStatus.NOTREADY);

        var user6aslobbyplayer = new PlayerInLobbyGetDTO();
        user6aslobbyplayer.setUsername("6");
        user6aslobbyplayer.setReadyStatus(GameStatus.NOTREADY);

        List<User> allUsers2 = new ArrayList<>();
        allUsers2.add(user6);

        game2 = new GameEntity(1L);
        game2.setId(2L);
        game2.setGameName("2");
        game2.setAllUsers(allUsers2);

        game2.getLobbyplayers().add(user6aslobbyplayer);

        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername1");
        testUser.setToken("123");
        testUser.setMoney(10);
        testUser.setGamestatus(GameStatus.NOTREADY);
        testUser.setWantsToShow(Show.NOT_DECIDED);

    }


    @Test
    void userLeavesLobby_success() throws Exception {

        // given
        var userPutDTO = new UserPutDTO();
        userPutDTO.setToken(testUser.getToken());

        given(lobbyService.getUserInSpecificGameSessionInAllUsers(Mockito.any(), Mockito.any())).willReturn(testUser);

        // test user leaves lobby of game 2
        MockHttpServletRequestBuilder putRequest = put("/lobbies/2/1/leave")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isNoContent());

    }

    @Test
    void userLeavesLobby_notFound() throws Exception {
        // user tries to leave wrong game
        var userPutDTO = new UserPutDTO();
        userPutDTO.setToken(testUser.getToken());

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(lobbyService).getUserInSpecificGameSessionInAllUsers(Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/lobbies/1/1/leave")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isNotFound());
    }

    @Test
    void userLeavesLobby_unauthorized() throws Exception {
        // user tries to leave wrong game
        var userPutDTO = new UserPutDTO();
        userPutDTO.setToken(testUser.getToken());

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED)).when(lobbyService).getUserInSpecificGameSessionInAllUsers(Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/lobbies/1/1/leave")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isUnauthorized());
    }

    @Test
    void userSetsUnready_success() throws Exception {
        // given
        var userPutDTO = new UserPutDTO();
        userPutDTO.setToken(testUser.getToken());

        given(lobbyService.getUserInSpecificGameSessionInAllUsers(Mockito.any(), Mockito.any())).willReturn(testUser);

        // test user leaves lobby of game 2
        MockHttpServletRequestBuilder putRequest = put("/lobbies/2/1/unready")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isNoContent());

    }

    @Test
    void userSetsUnready_notFound() throws Exception {
        // user tries to leave wrong game
        var userPutDTO = new UserPutDTO();
        userPutDTO.setToken(testUser.getToken());

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(lobbyService).getUserInSpecificGameSessionInAllUsers(Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/lobbies/2/1/unready")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isNotFound());

    }

    @Test
    void userSetsUnready_unauthorized() throws Exception {
        // user tries to leave wrong game
        var userPutDTO = new UserPutDTO();
        userPutDTO.setToken(testUser.getToken());

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED)).when(lobbyService).getUserInSpecificGameSessionInAllUsers(Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/lobbies/2/1/unready")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest).andExpect(status().isUnauthorized());

    }

    @Test
    void getLobbyOverviewSuccess() throws Exception {
        given(lobbyService.getAllGames()).willReturn(List.of(game, game2));

        MockHttpServletRequestBuilder getRequest = get("/lobbies").header("Authorization", "3");

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("1")))
                .andExpect(jsonPath("$[0].playerCount", is(5)))
                .andExpect(jsonPath("$[0].inGame", is(false)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("2")))
                .andExpect(jsonPath("$[1].playerCount", is(1)))
                .andExpect(jsonPath("$[1].inGame", is(false)));

    }

    @Test
    void getLobbyOverview_userIsNotFound(){
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE)).when(lobbyService).checkIfUserExistsByToken(Mockito.any());
        MockHttpServletRequestBuilder getRequest = get("/lobbies")
                .header("Authorization", "456");
        try {
            mockMvc.perform(getRequest).andExpect(status().isNotFound());
        }
        catch (Exception e) {
            fail();
        }

    }

    @Test
    void getMoreInformationAboutALobby_success() {
        given(lobbyService.getSpecificLobbyData(Mockito.anyLong())).willReturn(game2);

        MockHttpServletRequestBuilder getRequest = get("/lobbies/2").header("Authorization", "3");

        try{
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("2")))
                .andExpect(jsonPath("$.players[0].username", is("6")))
                .andExpect(jsonPath("$.players[0].readyStatus", is(GameStatus.NOTREADY.toString())))
                .andExpect(jsonPath("$.gameCanStart", is(false)))
                .andExpect(jsonPath("$.inGame", is(false)));}
        catch(Exception e){
            fail();
        }

    }

    @Test
    void getMoreInformationAboutALobby_userNotFound(){
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE)).when(lobbyService).checkIfUserExistsByToken(Mockito.any());
        MockHttpServletRequestBuilder getRequest = get("/lobbies/2")
                .header("Authorization", "456");
        try {
            mockMvc.perform(getRequest).andExpect(status().isNotFound());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void getMoreInformationAboutALobby_userIsNotInGameSession(){
        doThrow(new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User is not registered in the Lobby Session and therefore is not allowed to get more data about the Lobby!"))
                .when(lobbyService).checkIfUserIsInGameSession(Mockito.any(),Mockito.any());
        MockHttpServletRequestBuilder getRequest = get("/lobbies/2")
                .header("Authorization", "456");
        try {
            mockMvc.perform(getRequest).andExpect(status().isNotFound());
        }
        catch (Exception e) {
            fail();
        }
    }


    @Test
    void userJoinsLobby_Success(){
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("123");

        MockHttpServletRequestBuilder putRequest = put("/lobbies/2/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        given(lobbyService.getUserByTokenInUserRepository(Mockito.any())).willReturn(testUser);
        given(lobbyService.findGameEntity(Mockito.any())).willReturn(game2);

        try {
            MvcResult result = mockMvc.perform(putRequest)
                    .andExpect(status().isNoContent())
                    .andReturn();

            assertEquals("", result.getResponse().getContentAsString());
        }
        catch (Exception e) {

            fail();
        }
    }

    @Test
    void userJoinsLobby_userIsNotFound(){
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("123");

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE)).when(lobbyService).checkIfUserExistsByToken(Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/lobbies/2/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("404 NOT_FOUND \"The User could not be found...\"", result.getResolvedException().getMessage()));
        }
        catch (Exception e) {

            fail();
        }
    }

    @Test
    void userJoinsLobby_gameEntityWasNotFound(){
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("456");

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE_FOR_GAME)).when(lobbyService).findGameEntity(Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/lobbies/9/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("404 NOT_FOUND \"The GameSession could not be found...\"", result.getResolvedException().getMessage()));
        }
        catch (Exception e) {

            fail();
        }
    }

    @Test
    void userJoinsLobby_heIsAlreadyInAnOtherLobby(){
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("456");

        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "The User is playing in an other Lobby and therefore can not join this Lobby!"))
                .when(lobbyService).checkIfUserIsAlreadyInAnOtherLobby(Mockito.any(),Mockito.anyLong(),Mockito.any());


        MockHttpServletRequestBuilder putRequest = put("/lobbies/2/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isConflict())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("409 CONFLICT \"The User is playing in an other Lobby and therefore can not join this Lobby!\"",
                            result.getResolvedException().getMessage()));
        }
        catch (Exception e) {

            fail();
        }
    }

    @Test
    void userJoinsLobby_lobbyIsAlreadyFull(){
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("456");

        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "You can not join this Lobby, since the Session is already full!"))
                .when(lobbyService).addUserToGame(Mockito.any(),Mockito.any());


        MockHttpServletRequestBuilder putRequest = put("/lobbies/2/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isConflict())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("409 CONFLICT \"You can not join this Lobby, since the Session is already full!\"",
                            result.getResolvedException().getMessage()));
        }
        catch (Exception e) {

            fail();
        }
    }

    @Test
    void userJoinsLobby_sessionAlreadyStarted(){
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("456");

        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "You can not join this Lobby, since the Session has already started and the other Users are playing!"))
                .when(lobbyService).addUserToGame(Mockito.any(),Mockito.any());


        MockHttpServletRequestBuilder putRequest = put("/lobbies/2/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isConflict())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("409 CONFLICT \"You can not join this Lobby, since the Session has already started and the other Users are playing!\"",
                            result.getResolvedException().getMessage()));
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void userWantsToBeReady_success(){
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("123");

        MockHttpServletRequestBuilder putRequest = put("/lobbies/2/1/ready")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        given(lobbyService.getUserInSpecificGameSessionInAllUsers(Mockito.anyLong(),Mockito.any())).willReturn(testUser);
        given(lobbyService.findGameEntity(Mockito.any())).willReturn(game2);

        try {
            MvcResult result = mockMvc.perform(putRequest)
                    .andExpect(status().isNoContent())
                    .andReturn();

            assertEquals("", result.getResponse().getContentAsString());
        }
        catch (Exception e) {

            fail();
        }
    }

    @Test
    void userWantsToBeReady_userIsNotFound(){
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("456");

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE))
                .when(lobbyService).getUserInSpecificGameSessionInAllUsers(Mockito.anyLong(),Mockito.any());


        MockHttpServletRequestBuilder putRequest = put("/lobbies/2/9/ready")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("404 NOT_FOUND \"The User could not be found...\"",
                            result.getResolvedException().getMessage()));
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void userWantsToBeReady_gameIsNotFound(){
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("456");

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE_FOR_GAME))
                .when(lobbyService).findGameEntity(Mockito.anyLong());


        MockHttpServletRequestBuilder putRequest = put("/lobbies/9/1/ready")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals("404 NOT_FOUND \"The GameSession could not be found...\"",
                            result.getResolvedException().getMessage()));
        }
        catch (Exception e) {
            fail();
        }
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }

}