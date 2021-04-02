package ch.uzh.ifi.hase.soprafs21.service;



import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository){
        this.gameRepository = gameRepository;
    }

/*
    public User getUserById(Long id){
        Optional<User> returnUser=userRepository.findById(id);

        User returned = null;

        if (returnUser.isPresent()){
            returned = returnUser.get();
        }

        if(returned == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The User could not be found...");
        } else{
            return returned;}
    }
*/
/*
    public void userFolds(Long id){
        Optional<User> returnUser=userRepository.findById(id);

        /*
        1. there is the collection of Users that joined the Lobby in the first place

        2. there is the collection of Users that are still in the gameround

        if a User folds, he should still be in (1.) but not in (2.) anymore


    }*/

}
