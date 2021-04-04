package ch.uzh.ifi.hase.soprafs21.rest.dto;

/**
 * I (Carlos) didn't add 'private long id;' here, because you can get the id of a player via the URI (the query)
 */
public class UserPutDTO {

    private String token;

    private int raiseamount;

    public int getRaiseamount() {
        return raiseamount;
    }

    public void setRaiseamount(int raiseamount) {
        this.raiseamount = raiseamount;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
