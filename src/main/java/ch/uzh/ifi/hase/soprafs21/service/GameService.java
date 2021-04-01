package ch.uzh.ifi.hase.soprafs21.service;


import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class GameService {
    private final UserRepository userRepository;

    @Autowired
    private GameService(@Qualifier("userRepository") UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User getUserById(Long id){
        User returnUser=userRepository.findByid(id);
        if(returnUser == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The User could not be found...");
        } else{
            return returnUser;}
    }
}
