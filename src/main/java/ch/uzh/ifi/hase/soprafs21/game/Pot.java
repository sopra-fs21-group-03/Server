package ch.uzh.ifi.hase.soprafs21.game;

import ch.uzh.ifi.hase.soprafs21.entity.User;

import java.util.ArrayList;
import java.util.HashMap;

public class Pot {

    private HashMap<User, Integer> userContribution = new HashMap<>();

    private int total;

    public Pot() {
        total = 0;
    }

    public void addMoney(User user, int amount) {
        int currentContribution = userContribution.get(user);
        int newContribution = currentContribution + amount;
        userContribution.put(user, newContribution);

        total += amount;
    }

    private int sum() {
        return total;
    }

    public void addUser(User user) {
        if(userContribution.containsKey(user)) {
            throw new IllegalArgumentException("user already registered in pot");
        }
        userContribution.put(user, 0);
    }

    public void removeUser(User user) {
        if(!userContribution.containsKey(user)) {
            throw new IllegalArgumentException("user not registered in pot");
        }
        userContribution.remove(user);
    }

    //function receives an ordered array sorted by user with best cards to user with worst cards
    public void distribute(ArrayList<User> ranking) {
        ArrayList<User> enemies = (ArrayList<User>) ranking.clone();

        //gets next best user and gets all the money it is allowed to get, until no money is left in pot
        for(User user: ranking) {
            int invested = userContribution.get(user);
            int receives = 0;
            //user collects money for all users - including himself - from the pot
            for(User enemy: enemies) {
                receives += collect(enemy, invested);
            }
            user.addMoney(receives);
            if(total == 0) {
                break;
            }
        }
    }

    private int collect(User user, int amount) {
        int invested = userContribution.get(user);
        //common case, where the user that collects money, will receive all of a users money
        //also where a user should be collecting from what he put in the pot himself
        if(amount >= invested) {
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

    HashMap<User, Integer> getUserContribution() {
        return userContribution;
    }

    int getTotal() {
        return total;
    }
}
