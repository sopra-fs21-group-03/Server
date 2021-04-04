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
        for(User user: users) {
            user.addMoney(share);
            usersInDraw.put(user, usersInDraw.get(user) - getMinimum());
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
            usersInDraw.put(user, usersInDraw.get(user) - amount);
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
