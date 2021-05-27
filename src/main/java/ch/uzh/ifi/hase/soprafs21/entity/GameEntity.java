package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.*;
import ch.uzh.ifi.hase.soprafs21.game.Pot;
import ch.uzh.ifi.hase.soprafs21.game.cards.Deck;
import ch.uzh.ifi.hase.soprafs21.game.cards.River;
import ch.uzh.ifi.hase.soprafs21.game.protocol.ProtocolElement;
import ch.uzh.ifi.hase.soprafs21.helper.CardRanking;
import ch.uzh.ifi.hase.soprafs21.helper.UserDraw;
import ch.uzh.ifi.hase.soprafs21.rest.dto.OnTurnGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.OpponentInGameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.PlayerInLobbyGetDTO;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.security.SecureRandom;


@Entity
@Table(name = "GAME")
public class GameEntity implements Serializable, Name {

    private static final long serialVersionUID = 1L;
    private static final String ONE_MORE_CARD_MESSAGE = "One more card is dealt.";

    @Id
    private Long id;

    @Column
    private String gameName;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<User> activeUsers;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<User> allUsers;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<User> spectators;

    @ElementCollection
    private List<OpponentInGameGetDTO> playersInTurnOrder;

    @ElementCollection
    private List<PlayerInLobbyGetDTO> lobbyplayers;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<User> rawPlayersInTurnOrder;

    @Column
    private River river;

    @Column
    private Pot pot;

    @OneToOne
    private User userThatRaisedLast;

    @Column
    private OnTurnGetDTO onTurn;

    @Column
    private boolean showdown;

    @Column
    private boolean firstGameSetup;

    @Column
    private Deck deck;

    @Column
    private Round round;

    @Column
    private int checkcounter;

    @Column
    private boolean bigblindspecialcase;


    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProtocolElement> protocol;

    public void addProtocolElement(ProtocolElement element) {
        this.protocol.add(element);
    }

    public void setProtocol(List<ProtocolElement> protocol) {
        this.protocol = protocol;
    }

    public List<ProtocolElement> getProtocol() {
        return this.protocol;
    }

    /* Constructor */
    public GameEntity(Long id) {
        river = new River();
        allUsers = new ArrayList<>();
        activeUsers = new ArrayList<>();
        playersInTurnOrder = new ArrayList<>();
        lobbyplayers = new ArrayList<>();
        spectators = new ArrayList<>();
        this.id = id;
        firstGameSetup = true;
        bigblindspecialcase = true;
        protocol = new ArrayList<>();
        gameName = String.format("%d", id);
        deck = new Deck();
        pot = new Pot();
        round = Round.NOTSTARTED;
    }

    //Standard Constructor for spring
    public GameEntity() {

    }

    /* Getter and setter */

    public boolean getInGame() {
        return this.round != Round.NOTSTARTED && this.round != Round.ENDED;
    }

    public boolean getGameCanStart() {
        var readyCounter = 0;
        for (User user : allUsers) {
            if (user.getGamestatus() == GameStatus.READY) {
                readyCounter++;
            }
        }
        return allUsers.size() == 5 && readyCounter == 5;
    }

    public int getPlayerCount() {
        return allUsers.size() + spectators.size();
    }

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

    public void setAllUsers(List<User> allUsers) {
        this.allUsers = allUsers;
    }

    public List<User> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(List<User> activeUsers) {
        this.activeUsers = activeUsers;
    }

    public List<OpponentInGameGetDTO> getPlayersInTurnOrder() {
        return playersInTurnOrder;
    }

    public void setPlayersInTurnOrder(List<OpponentInGameGetDTO> opponents) {
        this.playersInTurnOrder = opponents;
    }

    public List<User> getSpectators() {
        return spectators;
    }

    public void setSpectators(List<User> spectators) {
        this.spectators = spectators;
    }

    public List<User> getRawPlayersInTurnOrder() {
        return rawPlayersInTurnOrder;
    }

    public List<PlayerInLobbyGetDTO> getLobbyplayers() {
        return lobbyplayers;
    }

    public void setLobbyplayers(List<PlayerInLobbyGetDTO> lobbyplayers) {
        this.lobbyplayers = lobbyplayers;
    }

    public void setRawPlayersInTurnOrder(List<User> rawPlayersInTurnOrder) {
        this.rawPlayersInTurnOrder = rawPlayersInTurnOrder;
    }

    public void setId(Long gameID) {
        this.id = gameID;
    }

    public Long getId() {
        return id;
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

    public boolean isBigblindspecialcase() {
        return bigblindspecialcase;
    }

    public void setBigblindspecialcase(boolean bigblindspecialcase) {
        this.bigblindspecialcase = bigblindspecialcase;
    }

    public boolean isFirstGameSetup() {
        return firstGameSetup;
    }

    public void setFirstGameSetup(boolean firstGameSetup) {
        this.firstGameSetup = firstGameSetup;
    }

    public String getName() {
        return "Dealer";
    }

    /**
     * @param theUser who has a partner in the gameround who is the potential next player in turn. If such a player exists, the function will return his
     *                username, else, the function will thrown an IllegalStateException.
     */
    public String getUsernameOfPotentialNextUserInTurn(User theUser) {
        if (activeUsers.size() > 1) {
            int indexOfPotentialNextUserInTurn;
            var user = getUserInActiveUsersWithId(theUser.getId());
            int index = -1;
            do {
                indexOfPotentialNextUserInTurn = Math.abs(activeUsers.indexOf(user) + index + activeUsers.size()) % activeUsers.size();
                index--;
                //the userThatRaisedLast should not be skipped!
                if (userThatRaisedLast != null && activeUsers.get(indexOfPotentialNextUserInTurn).getId().equals(userThatRaisedLast.getId())) {
                    return activeUsers.get(indexOfPotentialNextUserInTurn).getUsername();
                }
            } while (activeUsers.get(indexOfPotentialNextUserInTurn).getMoney() <= 0 && !activeUsers.get(indexOfPotentialNextUserInTurn).getUsername().equals(theUser.getUsername()));
            if (activeUsers.get(indexOfPotentialNextUserInTurn).getUsername().equals(theUser.getUsername())) {
                checkcounter = activeUsers.size();
            }
            return activeUsers.get(indexOfPotentialNextUserInTurn).getUsername();


        }
        throw new IllegalStateException("You cannot get the Username of the next User in turn since only one User or less are inside active Users and therefore, there" +
                "is no next User");
    }


    /**
     * @param usernameOfPotentialNextUserInTurn this is the username of the potential next user of the perspective of the User who called an Action, such
     *                                          as Fold, Raise, Check or Call.
     *                                          <p>
     *                                          Here, the next User how will be on turn will be set, the next round will be set if the previous round is finnished
     *                                          or if only one player is left in the activeUsers Array, he should be the winner and the function should handle this.
     *                                          <p>
     *                                          IMPORTANT ASSUMPTION: This username of this potentially next user in turn has to be in the activeUsers Array!
     */
    public void roundHandler(String usernameOfPotentialNextUserInTurn) {
        if (activeUsers.size() > 1) {
            if (checkcounter < getNumberOfActiveUsersWithMoney()) {
                int indexOfPotentialNextUserInTurn;
                var user = getUserInActiveUsersWithUsername(usernameOfPotentialNextUserInTurn);
                indexOfPotentialNextUserInTurn = activeUsers.indexOf(user);
                //if there is a user that raised last and its the user that is potentially next in turn -> he won't be in turn, because the next round has to start.
                if (userThatRaisedLast != null && activeUsers.get(indexOfPotentialNextUserInTurn).getUsername().equals(userThatRaisedLast.getUsername())) {
                    setNextRound();
                }
                else {
                    onTurn = new OnTurnGetDTO();
                    onTurn.setUsername(activeUsers.get(indexOfPotentialNextUserInTurn).getUsername());
                }
            }
            //everyone has checked/folded -> the next Round should start
            else {
                setNextRound();
            }
        }
        //there is only one active Player left -> give him his winnings
        else if (activeUsers.size() == 1) {
            //this remaining User has won
            //GIVE HIM HIS MONEY
            var winnerUserDraw = new UserDraw();
            winnerUserDraw.addUser(activeUsers.get(0), pot.getUserContributionOfAUser(activeUsers.get(0)));
            var winnerUser = activeUsers.get(0);
            ArrayList<UserDraw> winner = new ArrayList<>();
            winner.add(winnerUserDraw);
            protocol.add(new ProtocolElement(MessageType.LOG, this, "Now, the Pot will be distributed. We have the following Winner: "+winnerUser.getUsername()+". He won" +
                    " because he is the only player who stayed in this Game Round"));

            protocol.addAll(pot.distribute(winner));
            //then: a new gameround starts
            setup();
        }
        else {
            throw new IllegalStateException("There should always be atleast one active user!");
        }

    }

    public int getNumberOfActiveUsersWithMoney() {
        var counter = 0;
        for (User user : activeUsers) {
            if (user.getMoney() > 0) {
                counter++;
            }
        }
        return counter;
    }

    private User getUserInActiveUsersWithUsername(String username) {
        User returnUser = null;
        for (User user : activeUsers) {
            //I found the User who is potentially the next User in turn
            if (user.getUsername().equals(username)) {
                returnUser = user;
                break;
            }
        }
        return returnUser;
    }

    private User getUserInActiveUsersWithId(Long id) {
        User returnUser = null;
        for (User user : activeUsers) {
            //I found the User who is potentially the next User in turn
            if (user.getId().equals(id)) {
                returnUser = user;
                break;
            }
        }
        return returnUser;
    }


    private void setPlayerWhoStartsAtNewRound(User user) {
        if (user.getMoney() > 0) {
            onTurn = new OnTurnGetDTO();
            onTurn.setUsername(user.getUsername());
        }
        else {
            /**
             * This is again an All-In case. The User has no money left and therefore, the next user should start.
             */
            var nomoneycounter = 0;
            for (User startingUser : activeUsers) {
                if (startingUser.getMoney() == 0) {
                    nomoneycounter++;
                }
            }

            //Everybody went All-In or everybody except one person went All-In
            if (nomoneycounter == activeUsers.size() || nomoneycounter == (activeUsers.size() - 1)) {
                allInHandlerAtNewRound();
            }
            //Not everyone went All-In -> find the next User that is on turn
            else {
                // I assume: The method call below should never return the String "NextRoundPlease", since I already checked for the case that everybody went All-In

                String usernameOfUserWhoStartsTheRound = getUsernameOfPotentialNextUserInTurn(user);
                onTurn = new OnTurnGetDTO();
                onTurn.setUsername(usernameOfUserWhoStartsTheRound);

            }

        }

    }

    private void allInHandlerAtNewRound() {
        while (river.getCards().size() < 5) {
            river.addCard(deck.draw());
            this.protocol.add(new ProtocolElement(MessageType.LOG, this, ONE_MORE_CARD_MESSAGE));
        }
        //we directly get to the showdown
        round = Round.RIVERCARD;
        setNextRound();
    }

    //this always happens at the begin of a new round
    private void handlePlayerWhoStartsAtNewRound() {
        //is the SMALL Blind still in the activeUsers Array? allUsers certainly will contain the Small Blind
        int index = -1;
        User nextUser;
        for (User uservar : allUsers) {
            if (uservar.getBlind() == Blind.SMALL) {
                index = allUsers.indexOf(uservar);
                nextUser = uservar;
                if (activeUsers.contains(nextUser)) {
                    setPlayerWhoStartsAtNewRound(nextUser);
                    return;
                }
                else {
                    break;
                }
            }
        }
        var counter = 0;
        while (counter < allUsers.size()) {
            index = Math.abs((index - 1 + allUsers.size()) % (allUsers.size()));
            nextUser = allUsers.get(index);
            if (activeUsers.contains(nextUser)) {
                setPlayerWhoStartsAtNewRound(nextUser);
                return;
            }
            counter++;
        }

    }

    /**
     * The next Round should only start, if all Players made the same contribution
     */
    public void setNextRound() {
        setCheckcounter(0);

        if (round == Round.PREFLOP) {
            round = Round.FLOP;
            userThatRaisedLast = null;
            river.addCard(deck.draw());
            river.addCard(deck.draw());
            river.addCard(deck.draw());
            this.protocol.add(new ProtocolElement(MessageType.LOG, this, "Three cards are dealt."));
            handlePlayerWhoStartsAtNewRound();
        }

        else if (round == Round.FLOP) {
            round = Round.TURNCARD;
            userThatRaisedLast = null;
            river.addCard(deck.draw());
            this.protocol.add(new ProtocolElement(MessageType.LOG, this, ONE_MORE_CARD_MESSAGE));
            handlePlayerWhoStartsAtNewRound();
        }

        else if (round == Round.TURNCARD) {
            round = Round.RIVERCARD;
            userThatRaisedLast = null;
            river.addCard(deck.draw());
            this.protocol.add(new ProtocolElement(MessageType.LOG, this, ONE_MORE_CARD_MESSAGE));
            handlePlayerWhoStartsAtNewRound();
        }
        /**
         Here, we get inside the Showdown. This needs to be implemented
         */
        else if (round == Round.RIVERCARD) {
            this.protocol.add(new ProtocolElement(MessageType.LOG, this, "We have reached the Showdown! This is where the fun begins!"));
            round = Round.SHOWDOWN;
            showdown = true;
            allUsers.forEach(user -> user.setWantsToShow(Show.NOT_DECIDED));
            firstTimeNextUserInShowdown();
        }
        else if (round == Round.SHOWDOWN) {
            allUsers.forEach(user -> user.setWantsToShow(Show.NOT_DECIDED));
            setup();
            removeUserWithNoMoney();
        }
    }

    private void firstTimeNextUserInShowdown() {
        var user = getUserInAllUsersByName(onTurn.getUsername());
        if (user.isPresent()) {
            var u = user.get();
            int i = allUsers.indexOf(u);
            do {
                i = (i + allUsers.size() - 1) % allUsers.size();
            } while (!activeUsers.contains(allUsers.get(i)) && allUsers.get(i) != u);
            var onTurnNew = new OnTurnGetDTO();
            onTurnNew.setUsername(allUsers.get(i).getUsername());
            setOnTurn(onTurnNew);
        }
    }

    private Optional<User> getUserInAllUsersByName(String name) {
        User user = null;
        for (User u : allUsers) {
            if (name.equals(u.getUsername())) {
                user = u;
            }
        }
        return Optional.ofNullable(user);
    }


    /**
     * Setup function
     * Gets called when all players are in the game
     */
    public void setup() {
        if (firstGameSetup) {
            setStartingPotForUsers();
            pot = new Pot();
            setUpPot();
            List<User> players = new ArrayList<>(getAllUsers());
            setRawPlayersInTurnOrder(players);
        }
        if (numberOfBrokeUsersInAllUsers() + 1 == allUsers.size()) {
            /**
             * The Game Session has to end!
             */
            round = Round.ENDED;
            deck = new Deck();
            river.clear();
            showdown = false;
            bigblindspecialcase = true;
            protocol.add(new ProtocolElement(MessageType.LOG, this, "The GameSession has ended! User " + usernameOfUserWhoWon() + " has won!"));
            for (User user : rawPlayersInTurnOrder) {
                user.setGamestatus(GameStatus.NOTREADY);
            }
        }
        else {
            deck = new Deck();
            river.clear();
            round = Round.PREFLOP;
            showdown = false;
            bigblindspecialcase = true;
            distributeBlindsHandler();
            distributeCards();
            protocol.add(new ProtocolElement(MessageType.LOG, this, "New Gameround starts"));
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
                arrayuser.setGamestatus(GameStatus.NOTREADY);
                break;
            }
        }
    }

    public void removeUserFromSpectators(Long id) {
        for (User spectator : spectators) {
            if (spectator.getId().equals(id)) {
                spectators.remove(spectator);
                spectator.setGamestatus(GameStatus.NOTREADY);
                break;
            }
        }
    }

    public void removeUserFromRawPlayers(Long id) {
        for (User rawPlayer : rawPlayersInTurnOrder) {
            if (rawPlayer.getId().equals(id)) {
                spectators.remove(rawPlayer);
                rawPlayer.setGamestatus(GameStatus.NOTREADY);
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
            protocol.add(new ProtocolElement(MessageType.LOG, this, String.format("User %s joined the table", userToAdd.getUsername())));
        }
    }

    /**
     * Used to remove a user from an active list
     */
    public void removeUserFromAll(long id) {
        for (User arrayUser : allUsers) {
            if (arrayUser.getId().equals(id)) {
                allUsers.remove(arrayUser);
                arrayUser.setGamestatus(GameStatus.NOTREADY);
                break;
            }
        }
    }

    //set starting pot for all users

    /**
     * This function is used to set the starting pot for all users.
     * Currently sets it to 20'000.
     */
    private void setStartingPotForUsers() {
        for (User user : allUsers) {
            user.setMoney(5000);
        }
    }

    // Distribute blinds

    /**
     * Used to randomly distribute the small and big blind at the start of the game.
     * randomly distributes them at first gameSetup, in turn if not first game setup
     */

    private User getSmallBlindInAllUsers() {
        User returnUser = null;
        for (User user : allUsers) {
            if (user.getBlind() == Blind.SMALL) {
                returnUser = user;
            }
        }
        return returnUser;
    }

    private void distributeBlindsHandler() {
        if (firstGameSetup) {
            distributeBlindsFirstTime();
        }
        else {
            passBlind();
        }
    }

    private void passBlind() {
        int index;
        //Clone allUsers back into activeUsers to keep turn order right
        activeUsers = new ArrayList<>(allUsers);
        var smallie = getSmallBlindInAllUsers();
        index = allUsers.indexOf(smallie) + 1; //+1 because in the do while loop it is incremented in the beginning
        var toGetSmallBlind = getNewRole(index);
        index = allUsers.indexOf(toGetSmallBlind) + 1;
        var toGetBigBlind = getNewRole(index);
        /*
         * Assumption that we made but which is not always true: that this onTurn User is active (therefore, this User still has money)
         */
        index = allUsers.indexOf(toGetBigBlind) + 1;
        var onTurnUser = getNewRole(index);

        onTurn = new OnTurnGetDTO();
        onTurn.setUsername(onTurnUser.getUsername());

        for (User u : allUsers) {
            u.setBlind(Blind.NEUTRAL);
        }
        toGetSmallBlind.setBlind(Blind.SMALL);
        toGetBigBlind.setBlind(Blind.BIG);
        setUserThatRaisedLast(toGetBigBlind);
        protocol.add(new ProtocolElement(MessageType.LOG, this, toGetSmallBlind.getUsername()+" is the new Small Blind. 100 will be removed from his savings."));
        protocol.add(new ProtocolElement(MessageType.LOG, this, toGetBigBlind.getUsername()+" is the new Big Blind. 200 will be removed from his savings."));
        pot.addMoney(toGetBigBlind, toGetBigBlind.removeMoney(200));
        pot.addMoney(toGetSmallBlind, toGetSmallBlind.removeMoney(100));
    }

    private User getNewRole(int index) {
        do {
            index--;
        } while (allUsers.get(Math.abs((index - 1 + allUsers.size()) % (allUsers.size()))).getMoney() <= 0);
        return allUsers.get(Math.abs((index - 1 + allUsers.size()) % (allUsers.size())));
    }

    private void distributeBlindsFirstTime() {
        for (User user : allUsers) {
            user.setBlind(Blind.NEUTRAL);
        }
        // default value
        var randomInt = 1;
        // Generate random integer between 1 and 4
        var random = new SecureRandom();
        OptionalInt optionalRandomInt = random.ints(0, allUsers.size()).findFirst();
        if (optionalRandomInt.isPresent()) {
            randomInt = optionalRandomInt.getAsInt();
        }
        var toGetBigBlind = allUsers.get(randomInt);
        toGetBigBlind.setBlind(Blind.BIG);
        var toGetSmallBlind = allUsers.get(Math.abs((randomInt + 1) + allUsers.size()) % (allUsers.size()));
        toGetSmallBlind.setBlind(Blind.SMALL);
        onTurn = new OnTurnGetDTO();
        onTurn.setUsername(allUsers.get(Math.abs((randomInt - 1) + allUsers.size()) % (allUsers.size())).getUsername());
        protocol.add(new ProtocolElement(MessageType.LOG, this, toGetSmallBlind.getUsername()+" is the first Small Blind. 100 will be removed from his savings."));
        protocol.add(new ProtocolElement(MessageType.LOG, this, toGetBigBlind.getUsername()+" is the first Big Blind. 200 will be removed from his savings."));
        pot.addMoney(toGetBigBlind, toGetBigBlind.removeMoney(200));
        pot.addMoney(toGetSmallBlind, toGetSmallBlind.removeMoney(100));
        setUserThatRaisedLast(toGetBigBlind);
        firstGameSetup = false;

    }

    private void distributeCards() {

        for (User user : allUsers) {
            if (user.getCards().size() == 2) {
                user.getCards().clear();
            }
            for (var i = 0; i < 2; i++) {
                user.addCard(this.deck.draw());
            }
        }
    }

    private void setUpPot() {
        for (User user : allUsers) {
            pot.addUser(user);

        }
    }

    private int numberOfBrokeUsersInAllUsers() {
        var number = 0;
        for (User user : allUsers) {
            if (user.getMoney() == 0) {
                number++;
            }
        }
        return number;
    }

    /**
     * Assumption: One User has all the money and the rest of the Users don't have any money -> this User has won
     *
     * @return username of user who won
     */
    private String usernameOfUserWhoWon() {
        var name = "Nobody";
        for (User user : allUsers) {
            if (user.getMoney() > 0) {
                name = user.getUsername();
                break;
            }
        }
        return name;
    }

    public void distributePot() {
        activeUsers.removeIf(user -> user.getWantsToShow() != Show.SHOW);

        List<UserDraw> ranking = new CardRanking().getRanking(this);
        var index = 1;
        protocol.add(new ProtocolElement(MessageType.LOG, this, "Now, the Pot will be distributed. We have the following Winners of this Game Round:"));
        for(UserDraw userdrawers: ranking){
            for (User theUser: userdrawers.getUsers()){
                if(userdrawers.getUsers().size() == 1){
                    protocol.add(new ProtocolElement(MessageType.LOG, this, "User "+theUser.getUsername()+" is the "+ index +". winner"));
                } else{
                    protocol.add(new ProtocolElement(MessageType.LOG, this, "User "+theUser.getUsername()+" is, together with others, a "+ index +". winner"));
                }
            }
            index++;
        }
        protocol.addAll(pot.distribute(ranking));
    }

    /**
     * changes the onTurn user
     */
    public void nextTurnInShowdown(User user) throws IllegalStateException {
        if (!activeUsers.contains(user)) {
            throw new IllegalStateException("User that played was not onTurn.");
        }

        int currentIndex = activeUsers.indexOf(user);
        int nextIndex = Math.abs(currentIndex - 1 + activeUsers.size()) % activeUsers.size();
        onTurn.setUsername(activeUsers.get(nextIndex).getUsername());
    }

    /**
     * changes allUsers playing, that have no money left to spectators
     */
    private void removeUserWithNoMoney() {
        List<User> newSpectators = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getMoney() == 0 && getPot().getUserContributionOfAUser(user) == 0) {
                newSpectators.add(user);
            }
        }
        newSpectators.forEach(user -> user.getCards().clear());
        spectators.addAll(newSpectators);
        allUsers.removeAll(newSpectators);
        activeUsers.removeAll(newSpectators);
        for (User user : newSpectators) {
            protocol.add(new ProtocolElement(MessageType.LOG, this, String.format("User %s is now spectating", user.getUsername())));
        }
    }

    public void redistributePot() {
        pot.redistribute();

        protocol.add(new ProtocolElement(MessageType.LOG, this, "No one wanted to show, everyone gets their money back"));
    }
}

