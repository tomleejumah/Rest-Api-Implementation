package com.app.postmancollection.Model.ProjectModels;

import com.google.gson.annotations.SerializedName;

public class ArchivedAt {
    @SerializedName("Time")
    private String time;

    @SerializedName("Valid")
    private boolean valid;

    public ArchivedAt(String time, boolean valid) {
        this.time = time;
        this.valid = valid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
