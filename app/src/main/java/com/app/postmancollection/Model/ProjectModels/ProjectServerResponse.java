package com.app.postmancollection.Model.ProjectModels;

import com.app.postmancollection.Model.ProjectModels.ProjectData;
import com.app.postmancollection.Model.UserModels.UserCredentials;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProjectServerResponse {
    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<ProjectData> data = new ArrayList<>();

    public ProjectServerResponse() {
    }

    public ProjectServerResponse(int code, String message, List<ProjectData> data) {
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

    public List<ProjectData> getData() {
        return data;
    }

    public void setData(List<ProjectData> data) {
        this.data = data;
    }
}
