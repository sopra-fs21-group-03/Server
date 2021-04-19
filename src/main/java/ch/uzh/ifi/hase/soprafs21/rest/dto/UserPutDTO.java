package ch.uzh.ifi.hase.soprafs21.rest.dto;

/**
 * I (Carlos) didn't add 'private long id;' here, because you can get the id of a player via the URI (the query)
 */
public class UserPutDTO {

    private String token;

    private int raiseAmount;

    public int getRaiseAmount() {
        return raiseAmount;
    }

    public void setRaiseAmount(int raiseAmount) {
        this.raiseAmount = raiseAmount;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
