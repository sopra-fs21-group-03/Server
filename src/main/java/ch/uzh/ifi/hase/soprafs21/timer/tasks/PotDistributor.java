package ch.uzh.ifi.hase.soprafs21.timer.tasks;

import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;

public class PotDistributor implements Runnable{

    private final GameEntity gameEntity;
    private final GameRepository gameRepository;

    public PotDistributor(GameEntity gameEntity, GameRepository gameRepository) {
        this.gameEntity = gameEntity;
        this.gameRepository = gameRepository;
    }

    @Override
    public void run() {
        gameEntity.distributePot();
        gameEntity.setNextRound();

        gameRepository.saveAndFlush(gameEntity);

        System.out.println("I did my thing");
    }
}
