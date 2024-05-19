package com.app.postmancollection.Model.UserModels;

import com.google.gson.annotations.SerializedName;

public class UserCredentials {
    @SerializedName("Uuid")
    private String uuid;

    @SerializedName("Username")
    private String username;

    @SerializedName("Email")
    private String email;

    @SerializedName("Phone")
    private String phone;

    @SerializedName("Password")
    private String password;

    @SerializedName("Confirm_password")
    private String confirmPassword;

    @SerializedName("Username_or_email")
    private String usernameOrEmail;

    @SerializedName("access_token")
    private String accessToken;

    public UserCredentials(String uuid, String username, String email, String phone, String password,
                           String confirmPassword, String usernameOrEmail, String accessToken) {
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.usernameOrEmail = usernameOrEmail;
        this.accessToken = accessToken;
    }
    public UserCredentials(){

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
