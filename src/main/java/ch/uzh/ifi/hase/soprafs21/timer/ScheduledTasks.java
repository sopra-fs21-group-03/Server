package ch.uzh.ifi.hase.soprafs21.timer;

import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTasks {
    private final GameRepository gameRepository;
    private final GameService gameService;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    public ScheduledTasks(@Qualifier("gameRepository") GameRepository gameRepository, GameService gameService) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
    }


    @Scheduled(fixedRate = 5000)
    private void skipUserIfAFK(){
        var game = gameRepository.findById(1L);

        if (game.isEmpty()){
            return;
        }

        var trueGame = game.get();
        var onTurn = trueGame.getOnTurn();

        if (onTurn == null){
            return;
        }
        // Search for user
        for (var user : trueGame.getActiveUsers()){
            if (user.getUsername().equals(onTurn.getUsername())){
                gameService.userFolds(1L, user.getId());
                break;
            }
        }
        log.info("The time is now {}", dateFormat.format(new Date()));
    }
}
