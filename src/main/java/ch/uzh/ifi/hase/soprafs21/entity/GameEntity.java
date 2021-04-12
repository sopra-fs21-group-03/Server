package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.Blind;
import ch.uzh.ifi.hase.soprafs21.constant.Round;
import ch.uzh.ifi.hase.soprafs21.game.Pot;
import ch.uzh.ifi.hase.soprafs21.game.cards.Deck;
import ch.uzh.ifi.hase.soprafs21.game.cards.River;
import ch.uzh.ifi.hase.soprafs21.helper.CardRanking;
import ch.uzh.ifi.hase.soprafs21.helper.UserDraw;
import ch.uzh.ifi.hase.soprafs21.rest.dto.OnTurnGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.OpponentInGameGetDTO;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;

@Entity
@Table(name = "GAME")
public class GameEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long Id;

    //private Dealer dealer; maybe not necessary?!

    @Column
    private String gameName;

    @ElementCollection
    private List<User> activeUsers;

    @ElementCollection
    private List<User> allUsers;

    @ElementCollection
    private List<OpponentInGameGetDTO> opponents;

    @Column
    private River river;

    @Column
    private Pot pot;

    @OneToOne
    private User userThatRaisedLast;

    @Column // Maybe don't needed idk
    private OnTurnGetDTO onTurn;

    @Column
    private boolean showdown;

    @Column
    private boolean firstGameSetup;

    @Column
    private boolean setUpped;

    @Column
    private Deck deck;

    @Column
    private Round round;

    @Column
    private int checkcounter;


    /* Constructor */
    public GameEntity() {
        river = new River();
        allUsers = new ArrayList<>();
        activeUsers = new ArrayList<>();
        opponents = new ArrayList<>();
        Id = 1L;
        firstGameSetup = true;
        setUpped = false;
        deck = new Deck();
        pot = new Pot();

        round = Round.NOTSTARTED;
    }

    /* Getter and setter */

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    public int getCheckcounter() {
        return checkcounter;
    }

    public void setCheckcounter(int checkcounter) {
        this.checkcounter = checkcounter;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public User getUserThatRaisedLast() {
        return userThatRaisedLast;
    }

    public void setUserThatRaisedLast(User userThatRaisedLast) {
        this.userThatRaisedLast = userThatRaisedLast;
    }

    public List<User> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(ArrayList<User> allUsers) {
        this.allUsers = allUsers;
    }

    public List<User> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(ArrayList<User> activeUsers) {
        this.activeUsers = activeUsers;
    }

    public List<OpponentInGameGetDTO> getOpponents() {
        return opponents;
    }

    public void setOpponents(List<OpponentInGameGetDTO> opponents) {
        this.opponents = opponents;
    }

    public void setId(Long gameID) {
        this.Id = gameID;
    }

    public Long getId() {
        return Id;
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

    public void setOnTurn(OnTurnGetDTO current) {
        this.onTurn = current;
    }

    public OnTurnGetDTO getOnTurn() {
        return onTurn;
    }

    public void setShowdown(boolean bool) {
        this.showdown = bool;
    }

    public boolean getShowdown() {
        return showdown;
    }

    /**
     * @param theUser: The User who calls this function. Happens after Call, Check, Raise or Fold of this User
     */
    public void setNextUserOrNextRoundOrSomeoneHasAlreadyWon(User theUser) {
        if (activeUsers.size() > 1) {
            if (checkcounter < activeUsers.size()) {
                int indexOfPotentialNextUserInTurn;
                for (User user : activeUsers) {
                    //I found the User who performed the action
                    if (user.getId().equals(theUser.getId())) {
                        //give me the index of the potential next user
                        indexOfPotentialNextUserInTurn = Math.abs(activeUsers.indexOf(user) - 1 + activeUsers.size()) % activeUsers.size();
                        if (userThatRaisedLast != null && activeUsers.get(indexOfPotentialNextUserInTurn).getUsername().equals(userThatRaisedLast.getUsername())) {
                            setNextRound();
                        }
                        else {
                            onTurn = new OnTurnGetDTO();
                            onTurn.setUsername(activeUsers.get(indexOfPotentialNextUserInTurn).getUsername());
                        }
                    }
                }
            }
            else if (checkcounter == activeUsers.size()) {
                setNextRound();
            }
            else {
                throw new IllegalStateException("Something is wrong! The checkcounter should never be bigger than the Number of active Players!");
            }
        }
        else if (activeUsers.size() == 1) {
            //this remaining User has won
            //GIVE HIM HIS MONEY
            UserDraw winnerUserDraw = new UserDraw();
            winnerUserDraw.addUser(activeUsers.get(0), pot.getUserContributionOfAUser(activeUsers.get(0)));
            ArrayList<UserDraw> winner = new ArrayList<UserDraw>();
            winner.add(winnerUserDraw);
            pot.distribute(winner);
            //does this work???

            //then: a new gameround starts
            setUpped = false;
            try {
                setup();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        else {
            throw new IllegalStateException("There should always be atleast one active user!");
        }
    }

    //this always happens at the begin of a new round
    private void setSmallBlindAsPlayerInTurn() {
        for (User user : activeUsers) {
            if (user.getBlind() == Blind.SMALL) {
                onTurn = new OnTurnGetDTO();
                onTurn.setUsername(user.getUsername());
                break;
            }
        }
    }

    /**
     * The next Round should only start, if all Players made the same contribution
     */
    private void setNextRound() {

        if (round == Round.PREFLOP) {
            round = Round.FLOP;
            userThatRaisedLast = null;
            try {
                river.addCard(deck.draw());
                river.addCard(deck.draw());
                river.addCard(deck.draw());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            setSmallBlindAsPlayerInTurn();
        }

        else if (round == Round.FLOP) {
            round = Round.TURNCARD;
            userThatRaisedLast = null;
            try {
                river.addCard(deck.draw());

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            setSmallBlindAsPlayerInTurn();
        }

        else if (round == Round.TURNCARD) {
            round = Round.RIVERCARD;
            userThatRaisedLast = null;
            try {
                river.addCard(deck.draw());

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            setSmallBlindAsPlayerInTurn();
        }
        /**
         Here, we get inside the Showdown. This needs to be implemented
         */
        else if (round == Round.RIVERCARD) {
            round = Round.SHOWDOWN;
            showdown = true;
        }
        else if (round == Round.SHOWDOWN) {
            setUpped = false;
            try {
                setup();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * Setup function
     * Gets called when all players are in the game
     */
    public void setup() throws Exception {
        if (!setUpped) {
            setStartingPotForUsers();

            if (firstGameSetup) {
                setUpPot();
            }
            round = Round.PREFLOP;
            distributeBlinds();
            distributeCards();
            setUpped = true;
        }
    }

    /* Helper functions to set up a game */

    // Add/Remove users to/from the game

    /**
     * This function is used to add a user to the active list
     *
     * @param userToAdd user that should be added
     */
    public void addUserToActive(User userToAdd) {
        if (!activeUsers.contains(userToAdd)) {
            activeUsers.add(userToAdd);
        }
    }

    /**
     * This function is used to remove a user from the active list
     *
     * @param id id of the user that has to be removed
     */
    public void removeUserFromActive(Long id) {
        for (User arrayuser : activeUsers) {
            if (arrayuser.getId().equals(id)) {
                activeUsers.remove(arrayuser);
                break;
            }
        }
    }

    /**
     * Used to add a User to the all List
     *
     * @param userToAdd user to be added
     */
    public void addUserToAll(User userToAdd) {
        if (!allUsers.contains(userToAdd)) {
            allUsers.add(userToAdd);
        }
    }

    /**
     * Used to remove a user from an active list
     */
    public void removeUserFromAll(long id) {
        for (User arrayUser : allUsers) {
            if (arrayUser.getId().equals(id)) {
                allUsers.remove(arrayUser);
                break;
            }
        }
    }

    //set starting pot for all users

    /**
     * This function is used to set the starting pot for all users.
     * Currently sets it to 20'000.
     *
     * @throws Exception lobby is not full
     */
    private void setStartingPotForUsers() throws Exception {
        // Check if enough users are in the game
        if (allUsers.size() != 5) {
            throw new Exception();
        }

        for (User user : allUsers) {
            user.setMoney(5000);
        }
    }

    // Distribute blinds

    /**
     * Used to randomly distribute the small and big blind at the start of the game.
     * ... to be further implemented
     */
    private void distributeBlinds() throws Exception {
        // Check if enough users are in the game
        if (allUsers.size() != 5) {
            throw new Exception();
        }

        if (firstGameSetup) {
            for (User user : allUsers) {
                user.setBlind(Blind.NEUTRAL);
            }

            // default value
            int randomInt = 1;

            // Generate random integer between 1 and 4
            Random random = new Random();
            OptionalInt optionalRandomInt = random.ints(0, allUsers.size()).findFirst();

            if (optionalRandomInt.isPresent()) {
                randomInt = optionalRandomInt.getAsInt();
            }

            User toGetBigBlind = allUsers.get(randomInt);
            toGetBigBlind.setBlind(Blind.BIG);

            User toGetSmallBlind = allUsers.get(Math.abs((randomInt + 1) + activeUsers.size()) % (allUsers.size()));
            toGetSmallBlind.setBlind(Blind.SMALL);

            onTurn = new OnTurnGetDTO();
            onTurn.setUsername(allUsers.get(Math.abs((randomInt - 1) + activeUsers.size()) % (allUsers.size())).getUsername());

            pot.addMoney(toGetBigBlind, toGetBigBlind.removeMoney(200));
            pot.addMoney(toGetSmallBlind, toGetSmallBlind.removeMoney(100));
            setUserThatRaisedLast(toGetBigBlind);
            firstGameSetup = false;

        }
        else {
            int index;

            for (User user : allUsers) {
                if (user.getBlind() == Blind.SMALL) {
                    index = allUsers.indexOf(user);
                    allUsers.get(index).setBlind(Blind.NEUTRAL);

                    User toGetSmallBlind = allUsers.get(Math.abs((index - 1 + activeUsers.size()) % (allUsers.size())));
                    User toGetBigBlind = allUsers.get(Math.abs((index - 2 + activeUsers.size()) % (allUsers.size())));

                    /**
                     * Assumption that we made but which is not always true: that this onTurn User is active (therefore, this User still has money)
                     */
                    onTurn = new OnTurnGetDTO();
                    onTurn.setUsername(allUsers.get(Math.abs((index - 3 + activeUsers.size()) % (allUsers.size()))).getUsername());

                    toGetSmallBlind.setBlind(Blind.SMALL);
                    toGetBigBlind.setBlind(Blind.BIG);

                    pot.addMoney(toGetBigBlind, toGetBigBlind.removeMoney(200));
                    pot.addMoney(toGetSmallBlind, toGetSmallBlind.removeMoney(100));
                    break;
                }

            }
        }

    }

    private void distributeCards() throws Exception {
        // Check if enough users are in the game
        if (allUsers.size() != 5) {
            throw new Exception();
        }

        for (User user : allUsers) {
            for (int i = 0; i < 2; i++) {
                user.addCard(this.deck.draw());
            }
        }
    }

    private void setUpPot() {
        for (User user : allUsers) {
            pot.addUser(user);

        }
    }
}

