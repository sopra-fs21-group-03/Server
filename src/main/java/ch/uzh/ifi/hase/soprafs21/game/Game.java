package ch.uzh.ifi.hase.soprafs21.game;

import ch.uzh.ifi.hase.soprafs21.entity.User;

import java.util.ArrayList;

public class Game {
    //private Dealer dealer;
    private ArrayList<User> AllUsers;
    private ArrayList<User> ActiveUsers;

    public Game(){


    }


    public void removeUser(Long id){
        for (User arrayuser : ActiveUsers) {
            if (arrayuser.getId().equals(id)){
                ActiveUsers.remove(arrayuser);
                break;
            }
        }
    }


}
