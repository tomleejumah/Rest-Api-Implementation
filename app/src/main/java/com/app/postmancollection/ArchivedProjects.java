package com.app.postmancollection;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.postmancollection.Adapters.ProjectAdapter;
import com.app.postmancollection.Api.MyApiClient;
import com.app.postmancollection.Inteface.OnItemClickListener;
import com.app.postmancollection.Inteface.UnAuthorizedResponseHandler;
import com.app.postmancollection.Model.ProjectModels.ProjectData;
import com.app.postmancollection.Model.ProjectModels.ProjectServerResponse;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArchivedProjects extends AppCompatActivity implements OnItemClickListener {
    private MyApiClient myApiClient;
    RecyclerView rcArchivedPosts;
    ProjectAdapter projectAdapter;
    private List<ProjectData> projectData;
    private String BEARER_TOKEN;
    private static final String TAG = "ArchivedProjects";
    private UnAuthorizedResponseHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_archived_projects);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        myApiClient = MyApp.getMyApiClient();
        BEARER_TOKEN = getSharedPreferences("Access_Token", MODE_PRIVATE).getString("token", null);

        MaterialToolbar materialToolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(materialToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        materialToolbar.setNavigationOnClickListener(view -> finish());


        rcArchivedPosts = findViewById(R.id.rcArchivedPosts);
        projectData = new ArrayList<>();
        rcArchivedPosts.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcArchivedPosts.setLayoutManager(linearLayoutManager);
        Collections.reverse(projectData);
        projectAdapter = new ProjectAdapter(projectData,ArchivedProjects.this,R.layout.archived_project_item,ArchivedProjects.this);
        rcArchivedPosts.setAdapter(projectAdapter);

        getArchived();
    }
    public ArchivedProjects() {
    }
    void getArchived(){
        Call<ProjectServerResponse> getArchivedCall = myApiClient.listArchivedProjects("Bearer " + BEARER_TOKEN);
        getArchivedCall.enqueue(new Callback<ProjectServerResponse>() {
            @Override
            public void onResponse(Call<ProjectServerResponse> call, Response<ProjectServerResponse> response) {
                if (response.isSuccessful()) {
                    projectData.clear();
                    ProjectServerResponse projectResponse = response.body();
                    Log.d(TAG, "onResponse: " + projectResponse);
                    if (projectResponse != null) {
                        Log.d(TAG, "onResponse: Code: " + projectResponse.getCode());
                        Log.d(TAG, "onResponse: Message: " + projectResponse.getMessage());

                        if (projectResponse.getCode() == 0) {
                            List<ProjectData> projects = projectResponse.getData();
//                            for (ProjectData project : projects) {
                                projectData.addAll(projects);
                                projectAdapter.notifyDataSetChanged();
//                            }

                        } else {
                                Log.d(TAG, "Empty data response");
                            }
                        }
                } else {
                    handler.handleUnAuthorisedResponse(response);
                    Log.d(TAG, "onResponse: Request failed with code: " + response.code() + " and message: " + response.message());
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d(TAG, "onResponse: Error body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectServerResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: API call failed with error: " + t.getMessage());
            }
        });
    }

    void unarchiveProject(String projectId) {
        Call<ProjectServerResponse> unarchiveProjectCall = myApiClient.unarchiveProject(projectId, "Bearer " + BEARER_TOKEN);
        unarchiveProjectCall.enqueue(new Callback<ProjectServerResponse>() {
            @Override
            public void onResponse(Call<ProjectServerResponse> call, Response<ProjectServerResponse> response) {
                if (response.isSuccessful()) {
                    ProjectServerResponse unarchiveResponse = response.body();
                    if (unarchiveResponse != null) {
                        Log.d(TAG, "onResponse: Code: " + unarchiveResponse.getCode());
                        Log.d(TAG, "onResponse: Message: " + unarchiveResponse.getMessage());

                        if (unarchiveResponse.getCode() == 0) {
                            // Project unarchived successfully
                            Log.d(TAG, "Project unarchived successfully");
                            getArchived();

                        } else {
                            // Unarchive failed
                            Log.d(TAG, "onResponse: Error message: " + unarchiveResponse.getMessage());
                        }
                    }
                } else {
                    handler.handleUnAuthorisedResponse(response);
                    Log.d(TAG, "onResponse: Request failed with code: " + response.code() + " and message: " + response.message());
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d(TAG, "onResponse: Error body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectServerResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: API call failed with error: " + t.getMessage());
            }
        });
    }


    @Override
    public void onProjectItemClick(String projectUid, String userUid, String name, String description, String createdTime) {
        unarchiveProject(projectUid);
        projectAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskItemClick(String projectUid, String userUid, String name, String description, String createdTime) {

    }
}