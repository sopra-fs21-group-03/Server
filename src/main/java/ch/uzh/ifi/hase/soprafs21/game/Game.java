package ch.uzh.ifi.hase.soprafs21.game;

import ch.uzh.ifi.hase.soprafs21.entity.User;

import java.util.ArrayList;

public class Game {
    //private Dealer dealer;
    private ArrayList<User> ActiveUsers;

    public Game(){


    }

    /**
     * If a player folds, he should get removed?
     * @param id
     */
    public void removeUser(Long id){
        for (User arrayuser : ActiveUsers) {
            if (arrayuser.getId().equals(id)){
                ActiveUsers.remove(arrayuser);
                break;
            }
        }
    }


}
