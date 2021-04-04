package ch.uzh.ifi.hase.soprafs21.helper;

import ch.uzh.ifi.hase.soprafs21.entity.User;

import java.util.*;

public class UserDraw {
    private HashMap<User, Integer> usersInDraw = new HashMap();

    public void addUser(User user, int amount) {
        usersInDraw.put(user, amount);
    }

    public int getMinimum() {
        Collection<Integer> values = usersInDraw.values();
        int min = -1;
        for(int value: values) {
            if(min == -1) {
                min = value;
            }
            else if(value < min) {
                min = value;
            }
        }
        return min;
    }

    public Set<User> getUsers() {
        return usersInDraw.keySet();
    }

    public boolean empty() {
        return usersInDraw.isEmpty();
    }

    public void addMoneyAndDistribute(int amount) {
        Set<User> users = usersInDraw.keySet();
        int size = users.size();
        int share = amount / size;
        int minimum = getMinimum();
        for(User user: users) {
            user.addMoney(share);
            //int oldValue = usersInDraw.get(user);
            //usersInDraw.put(user, oldValue - minimum);
        }
        List<User> toBeRemoved = new ArrayList<>();
        for(User user: users) {
            if(usersInDraw.get(user) == 0) {
                toBeRemoved.add(user);
            }
        }
        for(User user: toBeRemoved) {
            usersInDraw.remove(user);
        }
    }

    public void subtract(int amount) {
        for(User user: usersInDraw.keySet()) {
            int oldValue = usersInDraw.get(user);
            usersInDraw.put(user, oldValue - amount);
        }
        List<User> usersToBeRemoved = new ArrayList<>();
        for(User user: usersInDraw.keySet()) {
            if(usersInDraw.get(user) <= 0) {
                usersToBeRemoved.add(user);
            }
        }
        for(User user: usersToBeRemoved) {
            usersInDraw.remove(user);
        }
    }
}
