package com.app.postmancollection.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AuthErrorDetails {
    @SerializedName("username_or_email")
    private List<String> usernameOrEmail;

    @SerializedName("password")
    private List<String> password;

    public List<String> getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(List<String> usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public List<String> getPassword() {
        return password;
    }

    public void setPassword(List<String> password) {
        this.password = password;
    }
}
