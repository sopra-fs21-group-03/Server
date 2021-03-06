package ch.uzh.ifi.hase.soprafs21.game;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.protocol.ProtocolElement;
import ch.uzh.ifi.hase.soprafs21.helper.UserDraw;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.*;
import java.io.Serializable;


@Embeddable
public class Pot implements Serializable {

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private Map<User, Integer> userContribution;

    // Only for sending the map to the client.
    // NOT USED IN INTERNAL REPRESENTATION
    @ElementCollection
    @Transient
    private Map<String, Integer> contribution;

    private int total;

    public Pot() {
        userContribution = new HashMap<>();
        total = 0;
    }

    public void addMoney(User user, int amount) {
        int currentContribution = userContribution.get(user);
        int newContribution = currentContribution + amount;
        userContribution.put(user, newContribution);

        total += amount;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setUserContribution(Map<User, Integer> userContribution) {
        this.userContribution = userContribution;
    }

    public Map<User, Integer> getUserContribution() {
        return userContribution;
    }

    public Map<String, Integer> getContribution() {
        contribution = new HashMap<>();

        for (Map.Entry<User, Integer> entry : userContribution.entrySet()) {
            contribution.put(entry.getKey().getUsername(), entry.getValue());
        }

        return contribution;
    }

    public void setContribution(Map<String, Integer> userContributionGetter) {
        this.contribution = userContributionGetter;
    }

    public void addUser(User user) {
        if (userContribution.containsKey(user)) {
            throw new IllegalArgumentException("user already registered in pot");
        }
        userContribution.put(user, 0);
    }

    public void removeUser(User user) {
        if (!userContribution.containsKey(user)) {
            throw new IllegalArgumentException("user not registered in pot");
        }
        userContribution.remove(user);
    }

    //function receives an ordered array sorted by user with best cards to user with worst cards
    public List<ProtocolElement> distribute(List<UserDraw> ranking) {
        var protocolElements = new ArrayList<ProtocolElement>();
        Set<User> enemies = userContribution.keySet();

        //gets next best user and gets all the money it is allowed to get, until no money is left in pot
        for (UserDraw user : ranking) {
            while (!user.empty()) {
                int invested = user.getMinimum();
                var receives = 0;
                //user collects money for all users - including himself - from the pot
                for (User enemy : enemies) {
                    receives += collect(enemy, invested);
                }
                protocolElements.addAll(user.addMoneyAndDistribute(receives));
                for (UserDraw userDraw : ranking) {
                    userDraw.subtract(invested);
                }
            }
            if (total == 0) {
                break;
            }
        }
        if (total != 0) {
            for (Map.Entry<User, Integer> entry : userContribution.entrySet()) {
                entry.getKey().addMoney(entry.getValue());
                userContribution.put(entry.getKey(), 0);
            }
        }

        total = 0;
        return protocolElements;
    }

    public void redistribute(){
        for (Map.Entry<User, Integer> entry : userContribution.entrySet()){
            entry.getKey().addMoney(entry.getValue());
            userContribution.put(entry.getKey(), 0);
        }
        total = 0;
    }

    private int collect(User user, int amount) {
        int invested = userContribution.get(user);
        //common case, where the user that collects money, will receive all of a users money
        //also where a user should be collecting from what he put in the pot himself
        if (amount >= invested) {
            userContribution.put(user, 0);
            total -= invested;
            return invested;
        }
        //case, where the collector can not receive all the money, from a user
        else {
            int newInvested = invested - amount;
            userContribution.put(user, newInvested);
            total -= amount;
            return amount;
        }
    }


    public int getUserContributionOfAUser(User user) {
        return userContribution.get(user);
    }

}
