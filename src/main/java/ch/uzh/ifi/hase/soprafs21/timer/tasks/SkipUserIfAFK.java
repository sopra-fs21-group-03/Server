package ch.uzh.ifi.hase.soprafs21.timer.tasks;

import ch.uzh.ifi.hase.soprafs21.constant.Round;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.service.GameService;

public class SkipUserIfAFK implements Runnable {
    private final GameRepository gameRepository;
    private final GameService gameService;
    private final long gameID;

    public SkipUserIfAFK(GameRepository gameRepository, GameService gameService, long gameID) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
        this.gameID = gameID;
    }


    @Override
    public void run() {
        var game = gameRepository.findById(gameID);

        if (game.isEmpty()) {
            return;
        }

        var trueGame = game.get();
        var onTurn = trueGame.getOnTurn();

        if (onTurn == null) {
            return;
        }
        // Search for user
        for (var user : trueGame.getActiveUsers()) {
            if (user.getUsername().equals(onTurn.getUsername())) {
                if (trueGame.getRound() == Round.SHOWDOWN){
                    gameService.show(trueGame, user, false);
                } else {
                    gameService.userFolds(trueGame, user.getId());
                }
                break;
            }
        }
    }

}
