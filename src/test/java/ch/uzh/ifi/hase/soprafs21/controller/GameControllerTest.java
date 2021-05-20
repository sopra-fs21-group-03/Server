package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs21.constant.Show;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.cards.Card;
import ch.uzh.ifi.hase.soprafs21.rest.dto.OpponentInGameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserShowPutDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
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

    private User testUser;

    
    /**
     * Test if own gameData could be fetched
     * Game set up
     */
    @Test
    void getOwnGameData_gameSetUp_success() throws Exception{
        //given
        GameEntity gameEntity = new GameEntity(1L);
        ArrayList<OpponentInGameGetDTO> playersInGame = new ArrayList<>();

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("test");
        user1.setToken("1");
        gameEntity.addUserToAll(user1);
        gameEntity.addUserToActive(user1);

        User user2 = new User();
        user2.setUsername("user1");
        user2.setPassword("test");
        user2.setToken("2");
        gameEntity.addUserToAll(user2);
        gameEntity.addUserToActive(user2);

        gameEntity.setGameName("default");

        //Game gets setUp with two users to mock case
        gameEntity.setup();

        // Mock gameEntity
        List<User> players = new ArrayList<>(gameEntity.getAllUsers());

        for (User player : players){
            playersInGame.add(DTOMapper.INSTANCE.convertEntityToOpponentInGameGetDTO(player));
        }

        gameEntity.setPlayersInTurnOrder(playersInGame);

        var mapped1 = DTOMapper.INSTANCE.convertEntityToPlayerInGameGetDTO(user1);

        given(gameService.getOwnGameData(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).willReturn(mapped1);

        //when
        MockHttpServletRequestBuilder getRequest = get("/games/1/1")
                .header("Authorization", user1.getToken());

        List<Card> userCards = user1.getCards();

        Card card1 = userCards.get(0);
        Card card2 = userCards.get(1);


        //then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user1.getUsername())))
                .andExpect(jsonPath("$.money", is(user1.getMoney())))
                .andExpect(jsonPath("$.blind", is(user1.getBlind().toString())))
                .andExpect(jsonPath("$.cards.[0]rank", is(card1.getRank().toString())))
                .andExpect(jsonPath("$.cards.[0]suit", is(card1.getSuit().toString())))
                .andExpect(jsonPath("$.cards.[1]rank", is(card2.getRank().toString())))
                .andExpect(jsonPath("$.cards.[1]suit", is(card2.getSuit().toString())));

    }

    /**
     * Test if own gameData could be fetched
     * Game not set up
     */
    @Test
    void getOwnGameData_gameNotSetUp_success() throws Exception{
        //given
        GameEntity gameEntity = new GameEntity(1L);
        ArrayList<OpponentInGameGetDTO> playersInGame = new ArrayList<>();

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("test");
        user1.setToken("1");
        gameEntity.addUserToAll(user1);
        gameEntity.addUserToActive(user1);

        gameEntity.setGameName("default");

        // Mock gameEntity
        List<User> players = new ArrayList<>(gameEntity.getAllUsers());

        for (User player : players){
            playersInGame.add(DTOMapper.INSTANCE.convertEntityToOpponentInGameGetDTO(player));
        }

        gameEntity.setPlayersInTurnOrder(playersInGame);

        var mapped1 = DTOMapper.INSTANCE.convertEntityToPlayerInGameGetDTO(user1);

        given(gameService.getOwnGameData(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).willReturn(mapped1);

        //when
        MockHttpServletRequestBuilder getRequest = get("/games/1/1")
                .header("Authorization", user1.getToken());

        //then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user1.getUsername())))
                .andExpect(jsonPath("$.money", is(user1.getMoney())))
                .andExpect(jsonPath("$.blind", is(user1.getBlind())))
                .andExpect(jsonPath("$.cards", is(user1.getCards())));
    }

    /**
     * Game or User could not be found
     */
    @Test
    void getOwnGameData_notFound() throws Exception{
        //given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("test");
        user1.setToken("1");

        given(gameService.getOwnGameData(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        //when
        MockHttpServletRequestBuilder getRequest = get("/games/1/1")
                .header("Authorization", user1.getToken());

        //then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    /**
     * User is not authorized to get this gameData
     */
    @Test
    void getOwnGameData_unauthorized() throws Exception{
        //given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("test");
        user1.setToken("1");

        given(gameService.getOwnGameData(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        //when
        MockHttpServletRequestBuilder getRequest = get("/games/1/1")
                .header("Authorization", user1.getToken());

        //then
        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }

    /**
     * Test if gameData is fetched in a set up game
     */
    @Test
    void getGameData_gameSetUp_success() throws Exception {
        //given
        GameEntity gameEntity = new GameEntity(1L);
        ArrayList<OpponentInGameGetDTO> playersInGame = new ArrayList<>();

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("test");
        user1.setToken("1");
        gameEntity.addUserToAll(user1);
        gameEntity.addUserToActive(user1);

        User user2 = new User();
        user2.setUsername("user1");
        user2.setPassword("test");
        user2.setToken("2");
        gameEntity.addUserToAll(user2);
        gameEntity.addUserToActive(user2);

        gameEntity.setGameName("default");

        //Game gets setUp with two users to mock case
        gameEntity.setup();

        // Mock gameEntity
        List<User> players = new ArrayList<>(gameEntity.getAllUsers());

        for (User player : players){
            playersInGame.add(DTOMapper.INSTANCE.convertEntityToOpponentInGameGetDTO(player));
        }

        gameEntity.setPlayersInTurnOrder(playersInGame);


        // will return the gameEntity
        given(gameService.getGameData(Mockito.anyLong(), Mockito.any())).willReturn(gameEntity);

        //when
        MockHttpServletRequestBuilder getRequest = get("/games/1")
                .header("Authorization", user1.getToken());

        //then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameName", is("default")))
                // Test if no cards are in the river
                .andExpect(jsonPath("$.river.cards", Matchers.empty()))
                // Test if hashmap in contribution is empty
                .andExpect(jsonPath("$.pot.contribution", is(gameEntity.getPot().getContribution())))
                // Expect the right amount in the pot
                .andExpect(jsonPath("$.pot.total", is(gameEntity.getPot().getTotal())))
                .andExpect(jsonPath("$.showdown", is(false)))
                // Either user1 or user2 is on turn
                .andExpect(jsonPath("$.onTurn.username", is(gameEntity.getOnTurn().getUsername())))
                .andExpect(jsonPath("$.round", is("PREFLOP")))
                // Check if the right opponent is displayed
                .andExpect(jsonPath("$.players.[0]username", is(user1.getUsername())))
                .andExpect(jsonPath("$.players.[0]money", is(user1.getMoney())))
                .andExpect(jsonPath("$.players.[0]blind", is(user1.getBlind().toString())))
                // Check if player is also displayed
                .andExpect(jsonPath("$.players.[1]username", is(user2.getUsername())))
                .andExpect(jsonPath("$.players.[1]money", is(user2.getMoney())))
                .andExpect(jsonPath("$.players.[1]blind", is(user2.getBlind().toString())));
    }

    /**
     * Test if gameData is fetched by a logged in user
     * In this test the game has not yet been instantiated
     */
    @Test
    void getGameData_gameNotSetUp_success() throws Exception {
        //given
        GameEntity gameEntity = new GameEntity(1L);
        ArrayList<OpponentInGameGetDTO> opponents = new ArrayList<>();

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("test");
        user1.setToken("1");
        gameEntity.addUserToAll(user1);
        gameEntity.addUserToActive(user1);

        User user2 = new User();
        user2.setUsername("user1");
        user2.setPassword("test");
        user2.setToken("2");
        gameEntity.addUserToAll(user2);
        gameEntity.addUserToActive(user2);

        // Mock gameEntity
        List<User> players = new ArrayList<>(gameEntity.getAllUsers());

        for (User player : players){
            opponents.add(DTOMapper.INSTANCE.convertEntityToOpponentInGameGetDTO(player));
        }

        gameEntity.setGameName("default");
        gameEntity.setPlayersInTurnOrder(opponents);

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken(user2.getToken());

        // will return the gameEntity
        given(gameService.getGameData(Mockito.anyLong(), Mockito.any())).willReturn(gameEntity);

        //when
        MockHttpServletRequestBuilder getRequest = get("/games/1")
                .header("Authorization", userPutDTO.getToken());

        // Mock map, pot contribution should be an empty map
        Map<User, Integer> mockedMap= new LinkedHashMap<>();

        //then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameName", is("default")))
                // Test if no cards are in the river
                .andExpect(jsonPath("$.river.cards", Matchers.empty()))
                // Test if hashmap in contribution is empty
                .andExpect(jsonPath("$.pot.contribution", is(mockedMap)))
                // Expect nothing in the pot
                .andExpect(jsonPath("$.pot.total", is(0)))
                .andExpect(jsonPath("$.showdown", is(false)))
                // No user is currently on turn
                .andExpect(jsonPath("$.onTurn", Matchers.nullValue()))
                .andExpect(jsonPath("$.round", is("NOTSTARTED")))
                // Check if the right opponent is displayed
                .andExpect(jsonPath("$.players.[0]username", is(user1.getUsername())))
                .andExpect(jsonPath("$.players.[0]money", is(user1.getMoney())))
                .andExpect(jsonPath("$.players.[0]blind", is(user1.getBlind())))
                // Check if player is also displayed
                .andExpect(jsonPath("$.players.[1]username", is(user2.getUsername())))
                .andExpect(jsonPath("$.players.[1]money", is(user2.getMoney())))
                .andExpect(jsonPath("$.players.[1]blind", is(user2.getBlind())));
    }
    /**
     * Test if game could not be found
     */
    @Test
    void getGameData_notFound() throws Exception {
        //given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("test");
        user1.setToken("1");

        given(gameService.getGameData(Mockito.anyLong(), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        //when
        MockHttpServletRequestBuilder getRequest = get("/games/1")
                .header("Authorization", user1.getToken());

        //then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());

    }

    /**
     * Test if user is not authorized to get gameData
     */
    @Test
    void getGameData_unauthorized() throws Exception {
        //given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("test");
        user1.setToken("1");

        given(gameService.getGameData(Mockito.anyLong(), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        //when
        MockHttpServletRequestBuilder getRequest = get("/games/1")
                .header("Authorization", "falseToken");

        //then
        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }

    /**
     * Parameterized test case for when fold, raise, call and check are not allowed
     * Makes tests more readable
     */
    @ParameterizedTest
    @CsvSource({
            // Parameters for fold
            "/games/1/1/fold, 401 UNAUTHORIZED \"The User is not found... (In Fold process)\", (In Fold process)",
            // Parameters for raise
            "/games/1/1/raise, 401 UNAUTHORIZED \"The User is not found... (In Raise process)\", (In Raise process)",
            // Parameters for call
            "/games/1/1/call, 401 UNAUTHORIZED \"The User is not found... (In Call process)\", (In Call process)",
            // Parameters for check
            "/games/1/1/check, 401 UNAUTHORIZED \"The User is not found... (In Check process)\", (In Check process)",
    })
    void userActionDuringGame_notAllowed(String uri, String errorMessage, String process){
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setToken("1");

        //given() doesn't work with a void function
        given(gameService.getUserByIdInActiveUsers(Mockito.any(), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED,"The User is not found... " + process));


        MockHttpServletRequestBuilder putRequest = put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        try {
            mockMvc.perform(putRequest)
                    .andExpect(status().isUnauthorized())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                    .andExpect(result -> assertEquals(errorMessage, result.getResolvedException().getMessage()))
            ;
        }
        catch (Exception e) {
            /*
             * Here, the Exception should not occur. If an Exception gets caught, the Test should fail.
             */

            fail();
        }
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
        userPutDTO.setRaiseAmount(10);

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
        userPutDTO.setRaiseAmount(10);

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
        userPutDTO.setRaiseAmount(10);

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
            /*
             * Here, the Exception should not occur. If an Exception gets caught, the Test should fail.
             */
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

    @Test
    void showTest() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername1");
        testUser.setToken("1");
        testUser.setMoney(10);
        testUser.setGamestatus(GameStatus.READY);
        testUser.setWantsToShow(Show.NOT_DECIDED);
        var testGame = new GameEntity(1L);
        testGame.addUserToAll(testUser);
        testGame.addUserToAll(testUser);

        var userShowPutDTO = new UserShowPutDTO();
        userShowPutDTO.setToken("1");
        userShowPutDTO.setWantsToShow(true);

        given(gameService.getUserByIdInActiveUsers(Mockito.any(), Mockito.any())).willReturn(testUser);
        given(gameService.getGameById(Mockito.any())).willReturn(testGame);

        MockHttpServletRequestBuilder putRequest = put("/games/1/1/show")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userShowPutDTO));

        try {
            MvcResult result = mockMvc.perform(putRequest)
                    .andExpect(status().isNoContent())
                    .andReturn();
        }
        catch (Exception e) {
            /**
             * Here, the Exception should not occur. If an Exception gets caught, the Test should fail.
             */
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