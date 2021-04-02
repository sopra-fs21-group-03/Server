package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name = "GAME")
public class GameEntity {

    @Id
    @GeneratedValue
    private Long gameID;

    //private Dealer dealer; maybe not necessary?!


    @Column
    private ArrayList<User> ActiveUsers;

    @Column(nullable = false)
    private ArrayList<User> AllUsers;

    public ArrayList<User> getAllUsers() {
        return AllUsers;
    }

    public void setAllUsers(ArrayList<User> allUsers) {
        AllUsers = allUsers;
    }

    public ArrayList<User> getActiveUsers() {
        return ActiveUsers;
    }

    public void setActiveUsers(ArrayList<User> activeUsers) {
        ActiveUsers = activeUsers;
    }

    public void setGameID(Long gameID) {
        this.gameID = gameID;
    }

    public Long getGameID() {
        return gameID;
    }


    public void removeUser(Long id) {
        for (User arrayuser : ActiveUsers) {
            if (arrayuser.getId().equals(id)) {
                ActiveUsers.remove(arrayuser);
                break;
            }
        }
    }


}

