package com.app.postmancollection.Model.UserModels;

import com.google.gson.annotations.SerializedName;

public class UserServerResponse {
    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private UserCredentials data;

    public UserServerResponse() {
    }
    public UserServerResponse(int code, String message, UserCredentials data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserCredentials getData() {
        return data;
    }

    public void setData(UserCredentials data) {
        this.data = data;
    }
}
