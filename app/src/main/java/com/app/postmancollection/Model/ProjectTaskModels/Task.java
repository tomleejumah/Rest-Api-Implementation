package com.app.postmancollection.Model.ProjectTaskModels;

import com.google.gson.annotations.SerializedName;

public class Task {
    @SerializedName("project_uuid")
    private String projectUuid;

    @SerializedName("name")
    private String name;

    @SerializedName("deadline")
    private String deadline;

    public Task() {
    }

    public Task(String projectUuid, String name, String deadline) {

    }
    public Task(String name, String deadline) {
    }

    // Getters and Setters
    public String getProjectUuid() {
        return projectUuid;
    }
    public void setProjectUuid(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getDeadline() {
        return deadline;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}

