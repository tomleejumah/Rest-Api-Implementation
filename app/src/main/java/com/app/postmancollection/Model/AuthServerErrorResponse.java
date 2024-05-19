package com.app.postmancollection.Model;

import com.google.gson.annotations.SerializedName;

public class AuthServerErrorResponse {
        @SerializedName("code")
        private int code;

        @SerializedName("message")
        private String message;

        @SerializedName("data")
        private AuthErrorDetails data;

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

    public AuthErrorDetails getData() {
        return data;
    }

    public void setData(AuthErrorDetails data) {
        this.data = data;
    }
}
