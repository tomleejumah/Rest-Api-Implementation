package com.app.postmancollection.Inteface;

public interface OnItemClickListener {
    void onProjectItemClick(String projectUid, String userUid, String name, String description, String createdTime);
    void onTaskItemClick(String projectUid, String userUid, String name, String description, String createdTime);
}
