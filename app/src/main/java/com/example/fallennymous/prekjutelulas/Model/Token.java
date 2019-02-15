package com.example.fallennymous.prekjutelulas.Model;

/**
 * Created by fallennymous on 14/02/2019.
 */

public class Token {
    private String token;
    private boolean ServerToken;

    public Token(){}

    public Token(String token, boolean serverToken) {
        this.token = token;
        ServerToken = serverToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isServerToken() {
        return ServerToken;
    }

    public void setServerToken(boolean serverToken) {
        ServerToken = serverToken;
    }
}
