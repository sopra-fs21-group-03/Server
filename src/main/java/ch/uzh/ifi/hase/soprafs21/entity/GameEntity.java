package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.game.Pot;
import ch.uzh.ifi.hase.soprafs21.game.cards.River;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GAME")
public class GameEntity {

    private static final long serialVersionUID = 1L;

    @Id
    private Long gameID;

    //private Dealer dealer; maybe not necessary?!

    @Column
    private String gameName;

    @ElementCollection
    private List<User> ActiveUsers;

    @ElementCollection
    private List<User> AllUsers;

    @Column
    private River river;

    @Column
    private Pot pot;

    @Column
    private User userThatRaisedLast;

    @Column // Maybe don't needed idk
    private User onTurn;

    @Column
    private boolean showdown;

    /* Constructor */
    public GameEntity(){
        AllUsers = new ArrayList<>();
        ActiveUsers = new ArrayList<>();
        gameID = 1L;

        pot = new Pot();
    }

    /* Getter and setter */

    public User getUserThatRaisedLast() {
        return userThatRaisedLast;
    }

    public void setUserThatRaisedLast(User userThatRaisedLast) {
        this.userThatRaisedLast = userThatRaisedLast;
    }

    public List<User> getAllUsers() {
        return AllUsers;
    }

    public void setAllUsers(ArrayList<User> allUsers) {
        AllUsers = allUsers;
    }

    public List<User> getActiveUsers() {
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

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public River getRiver() {
        return river;
    }

    public void setRiver(River river) {
        this.river = river;
    }


    public void setPot(Pot pot) {
        this.pot = pot;
    }

    public Pot getPot() {
        return pot;
    }

    public void setOnTurn(User current){
        this.onTurn = current;
    }

    public User getOnTurn(){
        return onTurn;
    }

    public void setShowdown(boolean bool){
        this.showdown = bool;
    }

    public boolean getShowdown(){
        return showdown;
    }

    /**
     * Setup function
     * Gets called when all players are in the game
     */
    public void setup(){

    }

    /* Helper functions to set up a game */

    // Add/Remove users to/from the game

    /**
     * This function is used to add a user to the active list
     * @param userToAdd user that should be added
     */
    public void addUserToActive(User userToAdd){
        if (!ActiveUsers.contains(userToAdd)){
            ActiveUsers.add(userToAdd);
        }
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
        if (!AllUsers.contains(userToAdd)){
            AllUsers.add(userToAdd);
        }
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
    private void setStartingPot() throws Exception {
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
    private void distributeBlinds(){

    }
}

