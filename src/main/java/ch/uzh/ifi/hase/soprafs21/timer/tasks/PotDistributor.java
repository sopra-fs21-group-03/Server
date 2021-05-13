package ch.uzh.ifi.hase.soprafs21.timer.tasks;

import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.service.GameService;


public class PotDistributor implements Runnable{

    private final GameEntity gameEntity;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GameService gameService;

    public PotDistributor(GameEntity gameEntity, GameRepository gameRepository, UserRepository userRepository, GameService gameService) {
        this.gameEntity = gameEntity;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.gameService = gameService;
    }

    @Override
    public void run() {
        gameEntity.distributePot();
        gameEntity.setNextRound();

        // Save all changes made to the repos
        for (User user : gameEntity.getAllUsers()){
            userRepository.saveAndFlush(user);
        }

        for (User user: gameEntity.getSpectators()){
            userRepository.saveAndFlush(user);
        }

        gameRepository.saveAndFlush(gameEntity);

    }
}
