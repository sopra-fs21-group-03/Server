package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.service.LobbyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        game = new GameEntity();
        game.setAllUsers(allUsers);

        var user6 = new User();
        user6.setUsername("6");

        List<User> allUsers2 = new ArrayList<>();
        allUsers2.add(user6);

        game2 = new GameEntity();
        game2.setAllUsers(allUsers2);

    }

    @Test
    void getLobbyOverviewSuccess() throws Exception {
        given(lobbyService.getAllGames()).willReturn(List.of(game, game2));

        MockHttpServletRequestBuilder getRequest = get("/lobbies");

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

}