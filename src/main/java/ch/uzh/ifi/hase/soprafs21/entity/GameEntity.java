package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.game.Pot;

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

    @Column
    private Pot pot;

    @Column
    private User userThatRaisedLast;

    @Column // Maybe don't needed idk
    private User currentTurn;

    @Column
    private boolean showdown;

    /* Getter and setter */

    public User getUserThatRaisedLast() {
        return userThatRaisedLast;
    }

    public void setUserThatRaisedLast(User userThatRaisedLast) {
        this.userThatRaisedLast = userThatRaisedLast;
    }

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

    public void setPot(Pot pot) {
        this.pot = pot;
    }

    public Pot getPot() {
        return pot;
    }

    public void setCurrentTurn(User current){
        this.currentTurn = current;
    }

    public User getCurrentTurn(){
        return currentTurn;
    }

    public void setShowdown(boolean bool){
        this.showdown = bool;
    }

    public boolean getShowdown(){
        return showdown;
    }

    /* Helper functions to set up a game */

    // Add/Remove users to/from the game
    /**
     * This function is used to add a user to the active list
     * @param userToAdd user that should be added
     */
    public void addUserToActive(User userToAdd){
        ActiveUsers.add(userToAdd);
    }

    /**
     * This function is used to remove a user from the active list
     * @param id id of the user that has to be removed
     */
    public void removeUserFromActive(Long id) {
        for (User arrayuser : ActiveUsers) {
            if (arrayuser.getId().equals(id)) {
                ActiveUsers.remove(arrayuser);
                break;
            }
        }
    }

    /**
     * Used to add a User to the all List
     * @param userToAdd user to be added
     */
    public void addUserToAll(User userToAdd){
        AllUsers.add(userToAdd);
    }

    /**
     * Used to remove a user from an active list
     */
    public void removeUserFromAll(long id){
        for (User arrayUser : AllUsers){
            if (arrayUser.getId().equals(id)){
                AllUsers.remove(arrayUser);
                break;
            }
        }
    }

    //set starting pot for all users

    /**
     * This function is used to set the starting pot for all users.
     * Currently sets it to 20'000.
     * @throws Exception lobby is not full
     */
    public void setStartingPot() throws Exception {
        if (AllUsers.size() != 5){
            throw new Exception();
        }

        for (User user : ActiveUsers){
            user.setMoney(20000);
        }
    }

    // Distribute blinds

    /**
     * Used to randomly distribute the small and big blind.
     * ... to be further implemented
     */
    public void distributeBlinds(){

    }
}

