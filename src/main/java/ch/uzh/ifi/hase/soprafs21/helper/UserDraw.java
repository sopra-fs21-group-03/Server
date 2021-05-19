package ch.uzh.ifi.hase.soprafs21.helper;

import ch.uzh.ifi.hase.soprafs21.constant.MessageType;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.protocol.ProtocolElement;

import java.io.Serializable;
import java.util.*;

public class UserDraw implements Serializable {

    private Map<User, Integer> usersInDraw = new HashMap<>();

    public void addUser(User user, int amount) {
        usersInDraw.put(user, amount);
    }

    public int getMinimum() {
        Collection<Integer> values = usersInDraw.values();
        int min = -1;
        for (int value : values) {
            if (min == -1 || value < min) {
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

    public List<ProtocolElement> addMoneyAndDistribute(int amount) {
        var protocolElements = new ArrayList<ProtocolElement>();
        Set<User> users = usersInDraw.keySet();
        int size = users.size();
        int share = amount / size;

        for (User user : users) {
            protocolElements.add(new ProtocolElement(MessageType.LOG, user, "User "+user.getUsername()+" receives "+share+"." +
                    " He previously had " + user.getMoney() + " and now has "+(user.getMoney()+share)+"."));
            user.addMoney(share);
        }
        List<User> toBeRemoved = new ArrayList<>();
        for (User user : users) {
            if (usersInDraw.get(user) == 0) {
                toBeRemoved.add(user);
            }
        }
        for (User user : toBeRemoved) {
            usersInDraw.remove(user);
        }
        return protocolElements;
    }

    public void subtract(int amount) {
        for (Map.Entry<User, Integer> theEntry : usersInDraw.entrySet()) {
            int oldValue = theEntry.getValue();
            usersInDraw.put(theEntry.getKey(), oldValue - amount);
        }

        List<User> usersToBeRemoved = new ArrayList<>();

        for (Map.Entry<User, Integer> entry : usersInDraw.entrySet()) {
            if (entry.getValue() <= 0) {
                usersToBeRemoved.add(entry.getKey());
            }
        }
        for (User user : usersToBeRemoved) {
            usersInDraw.remove(user);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof UserDraw)) {
            return false;
        }

        UserDraw o = (UserDraw) obj;
        List<User> users = new ArrayList<>(this.getUsers());
        List<User> otherUsers = new ArrayList<>(o.getUsers());

        for (User user : users) {
            if (!otherUsers.contains(user)) {
                return false;
            }
        }

        for (User otherUser : otherUsers) {
            if (!users.contains(otherUser)) {
                return false;
            }
        }
        return true;
    }
    @Override
    public int hashCode(){
        return super.hashCode();
    }
}
