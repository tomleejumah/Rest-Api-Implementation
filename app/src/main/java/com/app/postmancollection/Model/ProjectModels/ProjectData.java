package com.app.postmancollection.Model.ProjectModels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProjectData {
    @SerializedName("Uuid")
    private String uuid;

    @SerializedName("Name")
    private String name;

    @SerializedName("Description")
    private String description;

    @SerializedName("UserUuid")
    private String userUuid;

    @SerializedName("CreatedAt")
    private String createdAt;

    @SerializedName("ArchivedAt")
    private ArchivedAt archivedAt;

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ProjectData() {

    }

    public ProjectData(String uuid, String name, String description, String userUuid,
                       String createdAt, ArchivedAt archivedAt) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.userUuid = userUuid;
        this.createdAt = createdAt;
        this.archivedAt = archivedAt;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ArchivedAt getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(ArchivedAt archivedAt) {
        this.archivedAt = archivedAt;
    }
}
