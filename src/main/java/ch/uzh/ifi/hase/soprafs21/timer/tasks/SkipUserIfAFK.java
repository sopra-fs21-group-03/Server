package ch.uzh.ifi.hase.soprafs21.timer.tasks;

import ch.uzh.ifi.hase.soprafs21.constant.Round;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SkipUserIfAFK implements Runnable {
    private final GameRepository gameRepository;
    private final GameService gameService;


    private static final Logger log = LoggerFactory.getLogger(SkipUserIfAFK.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public SkipUserIfAFK(GameRepository gameRepository, GameService gameService) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
    }


    @Override
    public void run() {
        var game = gameRepository.findById(1L);

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
                    gameService.userFolds(1L, user.getId());
                }
                break;
            }
        }
        // Log date to see if timer is working
        log.info("The time is now {}", dateFormat.format(new Date()));
    }

}
