package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.Pot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Test
    void findByName_success() {
        // given
        GameEntity game = new GameEntity(1L);
        game.setAllUsers(new ArrayList<User>());
        game.setPot(new Pot());

        entityManager.persist(game);
        entityManager.flush();

        // when
        GameEntity foundGame;
        Optional<GameEntity> found = gameRepository.findById(game.getId());
        if(found.isPresent()) {
            foundGame = found.get();
        } else {
            foundGame = null;
        }

        // then
        assertNotNull(foundGame.getId());
        assertEquals(foundGame.getAllUsers(), game.getAllUsers());
        assertEquals(foundGame.getPot(), game.getPot());
    }
}