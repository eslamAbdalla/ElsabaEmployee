package com.elsabaautoservice.elsabaemployee;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class LogInUser {

    @SerializedName("username")
    private String username ;
    @SerializedName("password")
    private String password ;
    @SerializedName("result")
    private JsonObject result ;

    public LogInUser (){}

    public LogInUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public JsonObject getResult() {
        return result ;
    }

}
