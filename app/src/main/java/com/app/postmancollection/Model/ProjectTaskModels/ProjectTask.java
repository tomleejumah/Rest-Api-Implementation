package com.app.postmancollection.Model.ProjectTaskModels;

import com.app.postmancollection.Model.ProjectModels.ArchivedAt;

public class ProjectTask {
    private String Uuid;
    private String Name;
    private String Deadline;
    private String CreatedAt;
    private String ProjectUuid;
    private com.app.postmancollection.Model.ProjectModels.ArchivedAt ArchivedAt;
    private String ProjectName;

    public ProjectTask(String uuid, String name, String deadline, String createdAt, String projectUuid,
                       com.app.postmancollection.Model.ProjectModels.ArchivedAt archivedAt, String projectName) {
        Uuid = uuid;
        Name = name;
        Deadline = deadline;
        CreatedAt = createdAt;
        ProjectUuid = projectUuid;
        ArchivedAt = archivedAt;
        ProjectName = projectName;
    }

    public String getUuid() {
        return Uuid;
    }

    public void setUuid(String uuid) {
        Uuid = uuid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDeadline() {
        return Deadline;
    }

    public void setDeadline(String deadline) {
        Deadline = deadline;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getProjectUuid() {
        return ProjectUuid;
    }

    public void setProjectUuid(String projectUuid) {
        ProjectUuid = projectUuid;
    }

    public com.app.postmancollection.Model.ProjectModels.ArchivedAt getArchivedAt() {
        return ArchivedAt;
    }

    public void setArchivedAt(com.app.postmancollection.Model.ProjectModels.ArchivedAt archivedAt) {
        ArchivedAt = archivedAt;
    }

    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }
}
