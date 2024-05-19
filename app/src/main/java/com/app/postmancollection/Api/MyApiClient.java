package com.app.postmancollection.Api;

import com.app.postmancollection.Model.ProjectModels.Project;
import com.app.postmancollection.Model.ProjectModels.ProjectServerResponse;
import com.app.postmancollection.Model.ProjectTaskModels.Task;
import com.app.postmancollection.Model.ProjectTaskModels.ProjectTaskServerResponse;
import com.app.postmancollection.Model.UserModels.UserServerResponse;
import com.app.postmancollection.Model.UserModels.UserCredentials;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MyApiClient {
    @POST("register")
    Call<Void> registerUser(
            @Body UserCredentials userCredentials
    );

    @POST("login")
    Call<UserServerResponse> loginUser(
            @Body UserCredentials userCredentials
    );

    @GET("current_user")
    Call<UserServerResponse> getCurrentUser(@Header("Authorization") String authorization);


    @POST("projects")
    Call<ProjectServerResponse> createProject(
            @Header("Authorization") String authorization,
            @Body Project project
    );

    @GET("projects")
    Call<ProjectServerResponse> getProjects(
            @Header("Authorization") String authorization
    );

    @DELETE("projects/{projectUuid}")
    Call<ProjectServerResponse> archiveProject(
            @Path("projectUuid") String projectUuid,
            @Header("Authorization") String authorization
    );

    @GET("archived-projects")
    Call<ProjectServerResponse> listArchivedProjects(
            @Header("Authorization") String authorization
    );

    @DELETE("projects/{projectUuid}")
    Call<ProjectServerResponse> deleteArchivedProject(
            @Path("projectUuid") String projectUuid,
            @Header("Authorization") String authorization
    );

    @PUT("projects/{projectUuid}")
    Call<ProjectServerResponse> updateProject(
            @Path("projectUuid") String projectUuid,
            @Header("Authorization") String authorization,
            @Body Project project
    );

    @PUT("unarchive-project/{id}")
    Call<ProjectServerResponse> unarchiveProject(
            @Path("id") String projectId,
            @Header("Authorization") String authorization
    );

    @POST("project-tasks")
    Call<ProjectTaskServerResponse> createTask(
            @Header("Authorization") String authorization,
            @Body Task task
    );

    @GET("all-project-tasks")
    Call<ProjectTaskServerResponse> getAllTasks(
            @Header("Authorization") String authorization
    );

    @GET("project-tasks/{taskUuid}")
    Call<ProjectTaskServerResponse> getProjectTask(
            @Path("taskUuid") String taskUuid,
            @Header("Authorization") String authorization
    );

    @GET("archived-project-tasks/{projectUuid}")
    Call<ProjectTaskServerResponse> getArchivedProjectTasks(
            @Path("projectUuid") String projectUuid,
            @Header("Authorization") String authorization
    );

    @PUT("project-tasks/{id}")
    Call<ProjectTaskServerResponse> updateTask(
            @Path("id") String taskId,
            @Header("Authorization") String authorization,
            @Body Task taskUpdateRequest
    );

    @DELETE("project-tasks/{id}")
    Call<ProjectTaskServerResponse> deleteTask(
            @Path("id") String taskId,
            @Header("Authorization") String authorization
    );

    @PUT("unarchive-project-task/{id}")
    Call<ProjectTaskServerResponse> unarchiveProjectTask(
            @Path("id") String taskId,
            @Header("Authorization") String authorization
    );
}
