package com.app.postmancollection.Model.ProjectTaskModels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProjectTaskServerResponse {
    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<ProjectTask> data = new ArrayList<>();

    public ProjectTaskServerResponse(int code, String message, List<ProjectTask> data) {
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

    public List<ProjectTask> getData() {
        return data;
    }

    public void setData(List<ProjectTask> data) {
        this.data = data;
    }
}
