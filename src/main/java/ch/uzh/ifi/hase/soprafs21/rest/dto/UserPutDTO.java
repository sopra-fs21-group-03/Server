package ch.uzh.ifi.hase.soprafs21.rest.dto;


public class UserPutDTO {

    private String token;

    private int raiseAmount;

    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
