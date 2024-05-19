package com.app.postmancollection;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.postmancollection.Adapters.ProjectAdapter;
import com.app.postmancollection.Adapters.TaskAdapter;
import com.app.postmancollection.Api.MyApiClient;
import com.app.postmancollection.Inteface.OnItemClickListener;
import com.app.postmancollection.Inteface.UnAuthorizedResponseHandler;
import com.app.postmancollection.Model.ProjectModels.ProjectData;
import com.app.postmancollection.Model.ProjectModels.ProjectServerResponse;
import com.app.postmancollection.Model.ProjectTaskModels.ProjectTask;
import com.app.postmancollection.Model.ProjectTaskModels.ProjectTaskServerResponse;
import com.app.postmancollection.Model.UserModels.UserServerResponse;
import com.app.postmancollection.Model.UserModels.UserCredentials;
import com.app.postmancollection.Util.FetchNewToken;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements UnAuthorizedResponseHandler, OnItemClickListener {

    private RecyclerView rcNotes;
    private MyApiClient myApiClient;
    private static final String TAG = "MainActivity";
    private String BEARER_TOKEN;
    private List<ProjectData> projectData;
    private SwipeRefreshLayout swipeRefreshLayout;
    ProjectAdapter projectAdapter;

    FloatingActionButton mAddFab, fabAddProject, fabAddTask;
    TextView addProject, addTask,page_title,hint_text;
    Boolean isAllFabsVisible;
    private ImageView imgFilter;
    ConstraintLayout btnArchives;
    TaskAdapter taskAdapter;
    List<ProjectTask> projectTaskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        myApiClient = MyApp.getMyApiClient();

        BEARER_TOKEN = getSharedPreferences("Access_Token", MODE_PRIVATE).getString("token", null);

        swipeRefreshLayout = findViewById(R.id.main);
        rcNotes = findViewById(R.id.rcNotes);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getProjects();
            getAllTasks();
        });

        mAddFab = findViewById(R.id.fabAddNote);
        btnArchives = findViewById(R.id.btnArchives);

        // FAB button
        fabAddProject = findViewById(R.id.add_alarm_fab);
        fabAddTask = findViewById(R.id.add_person_fab);
        page_title = findViewById(R.id.page_title);
        hint_text = findViewById(R.id.hint_text);

        addProject = findViewById(R.id.add_alarm_action_text);
        addTask = findViewById(R.id.add_person_action_text);
        imgFilter = findViewById(R.id.imgFilter);

        fabAddProject.setVisibility(View.GONE);
        fabAddTask.setVisibility(View.GONE);
        addProject.setVisibility(View.GONE);
        addTask.setVisibility(View.GONE);

        isAllFabsVisible = false;

        getProjects();
        getAllTasks();

        //for project data

        projectData = new ArrayList<>();
        rcNotes.hasFixedSize();
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rcNotes.setLayoutManager(staggeredGridLayoutManager);
        Collections.reverse(projectData);
        projectAdapter = new ProjectAdapter(projectData,MainActivity.this,R.layout.projects_layouts,this);
        rcNotes.setAdapter(projectAdapter);

//for task data
        projectTaskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(false, projectTaskList, MainActivity.this,this);

        projectAdapter.notifyDataSetChanged();

        registerForContextMenu(imgFilter);

        findViewById(R.id.imgFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openContextMenu(v);
                showPopupMenu(v);
            }
        });

        findViewById(R.id.btnArchives).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();
                Intent intent = new Intent(MainActivity.this, ArchivedProjects.class);
                intent.putExtra("viewType", tag);
                startActivity(intent);
            }
        });


        mAddFab.setOnClickListener(view -> {
            if (!isAllFabsVisible) {
                // when isAllFabsVisible becomes true make all
                // the action name texts and FABs VISIBLE
                fabAddProject.show();
                fabAddTask.show();
                addProject.setVisibility(View.VISIBLE);
                addTask.setVisibility(View.VISIBLE);

                // make the boolean variable true as we
                // have set the sub FABs visibility to GONE
                isAllFabsVisible = true;
            } else {
                // when isAllFabsVisible becomes true make
                // all the action name texts and FABs GONE.
                fabAddProject.hide();
                fabAddTask.hide();
                addProject.setVisibility(View.GONE);
                addTask.setVisibility(View.GONE);

                // make the boolean variable false as we
                // have set the sub FABs visibility to GONE
                isAllFabsVisible = false;
            }
        });
        fabAddTask.setOnClickListener(
                view ->{
              shoAddTaskDialog();
                });

        // below is the sample action to handle add alarm FAB. Here it shows simple Toast msg
        // The Toast will be shown only when they are visible and only when user clicks on them
        fabAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, NoteDetails.class);
                intent1.putExtra("action", "create");
                startActivity(intent1);
            }
        });

        new Handler().postDelayed(() -> hint_text.setVisibility(View.GONE),10000);
}

    private void shoAddTaskDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_info);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg));
        dialog.setCancelable(true);

        dialog.show();

        Button btnConfirm=dialog.findViewById(R.id.btnCancel);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.main_menu, popupMenu.getMenu());

        // Handle menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //todo populate the recycleviews according to the option
                if (item.getItemId() == R.id.project) {
                    page_title.setText("Projects");
                    btnArchives.setTag("projects");
                    rcNotes.setLayoutManager(null);
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    rcNotes.setLayoutManager(staggeredGridLayoutManager);
                    rcNotes.setAdapter(projectAdapter);
                    projectAdapter.notifyDataSetChanged();
                    return true;
                } else if (item.getItemId() == R.id.task) {
                    btnArchives.setTag("tasks");
                    rcNotes.setLayoutManager(null);
                    rcNotes.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));;
                    rcNotes.setAdapter(taskAdapter);
                    taskAdapter.notifyDataSetChanged();
                   page_title.setText("All Tasks");
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Show the popup menu
        popupMenu.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hint_text.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> hint_text.setVisibility(View.GONE),10000);
        getProjects();
        getAllTasks();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // you can set menu header with title icon etc
//        menu.setHeaderTitle("Filter");
        // add menu items
        menu.add(0, v.getId(), 0, "Project");
        menu.add(0, v.getId(), 0, "Task");
    }

    void getProjects() {
        Call<ProjectServerResponse> getProjectsCall = myApiClient.getProjects("Bearer " + BEARER_TOKEN);
        getProjectsCall.enqueue(new Callback<ProjectServerResponse>() {
            @Override
            public void onResponse(Call<ProjectServerResponse> call, Response<ProjectServerResponse> response) {
                if (response.isSuccessful()) {
                    ProjectServerResponse projectResponse = response.body();
                    if (projectResponse != null) {
                        swipeRefreshLayout.setRefreshing(false);

                        if (projectResponse.getCode() == 0) {
                            projectData.clear();
                            // Code 0 data available
                            List<ProjectData> projects = projectResponse.getData();
                            if (projects != null) {
                                for (ProjectData project : projects) {

                                    projectData.add(new ProjectData(project.getUuid(), project.getName(), project.getDescription(),
                                            project.getUserUuid(), project.getCreatedAt(), project.getArchivedAt()));
                                    projectAdapter.notifyDataSetChanged();

                                }
                            }

                        } else {
                            Log.d(TAG, "Empty data response");
                        }
                    }
                } else {
                    handleUnAuthorisedResponse(response);
                    swipeRefreshLayout.setRefreshing(false);
                    Log.d(TAG, "onResponse: Request failed with code : " + response.code() + " and message: " + response.message());
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d(TAG, "onResponse: Error body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                projectAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ProjectServerResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: API call failed with error: " + t.getMessage());
            }
        });
    }
    public void archiveProject(Context context , String projectUuid) {
     String BEARER_TOKEN = context.getSharedPreferences("Access_Token", MODE_PRIVATE).getString("token", null);
        MyApiClient myApiClient = MyApp.getMyApiClient();
        Call<ProjectServerResponse> call = myApiClient.archiveProject(projectUuid, "Bearer " + BEARER_TOKEN);
        call.enqueue(new Callback<ProjectServerResponse>() {
            @Override
            public void onResponse(Call<ProjectServerResponse> call, Response<ProjectServerResponse> response) {
                if (response.isSuccessful()) {
                    ProjectServerResponse projectResponse = response.body();
                    if (projectResponse != null) {
                        Log.d(TAG, "onResponse: Code: " + projectResponse.getCode());
                        Log.d(TAG, "onResponse: Message: " + projectResponse.getMessage());
                        Toast.makeText(context, "Project archived successfully", Toast.LENGTH_SHORT).show();
                        getProjects();
                    }
                } else {
                    Log.d(TAG, "onResponse: Request failed with code: " + response.code() + " and message: " + response.message());
                    Toast.makeText(context, "Project Fail To Archive", Toast.LENGTH_SHORT).show();
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
    void getAllTasks() {
        String token = "Bearer " + BEARER_TOKEN;
        Call<ProjectTaskServerResponse> getAllTasksCall = myApiClient.getAllTasks(token);
        getAllTasksCall.enqueue(new Callback<ProjectTaskServerResponse>() {
            @Override
            public void onResponse(Call<ProjectTaskServerResponse> call, Response<ProjectTaskServerResponse> response) {
                if (response.isSuccessful()) {
                    ProjectTaskServerResponse taskListResponse = response.body();
                    if (taskListResponse != null) {
//                        Log.d(TAG, "onResponse: Code: " + taskListResponse.getCode());
//                        Log.d(TAG, "onResponse: Message: " + taskListResponse.getMessage());
                        projectTaskList.clear();
                        if (taskListResponse.getCode() == 0) {
                            List<ProjectTask> tasks = taskListResponse.getData();
                            Log.d(TAG, "JSON response: " + new Gson().toJson(tasks));
                            if (tasks != null) {
                                Log.d(TAG, "Task list size: " + tasks.size());
                                for (ProjectTask task : taskListResponse.getData()) {
                                    Log.d(TAG, "Task UUID: " + task.getUuid());
                                    Log.d(TAG, "Task Name: " + task.getName());
                                    projectTaskList.add(new ProjectTask(task.getUuid(), task.getName(), task.getDeadline(),
                                            task.getCreatedAt(), task.getProjectUuid(), task.getArchivedAt(), task.getProjectName()));
                                    taskAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Log.d(TAG, "onResponse: Error message: " + taskListResponse.getMessage());
                        }
                    }
                } else {
                    handleUnAuthorisedResponse(response);
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
            public void onFailure(Call<ProjectTaskServerResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: API call failed with error: " + t.getMessage());
            }
        });
    }
    void currentUser(){
        Call<UserServerResponse> currentUserCall = myApiClient.getCurrentUser("Bearer " + BEARER_TOKEN);
        currentUserCall.enqueue(new Callback<UserServerResponse>() {
            @Override
            public void onResponse(Call<UserServerResponse> call, Response<UserServerResponse> response) {
                if (response.isSuccessful()) {
                    UserServerResponse userServerResponseResponse = response.body();
                    if (userServerResponseResponse != null) {
                        int code = userServerResponseResponse.getCode();
                        String message = userServerResponseResponse.getMessage();
                        UserCredentials currentUser = userServerResponseResponse.getData();

                        if (currentUser != null) {
                            String uuid = currentUser.getUuid();
                            String username = currentUser.getUsername();
                            String email = currentUser.getEmail();
                            String phone = currentUser.getPhone();

                            Toast.makeText(getApplicationContext(), "UUID: " + uuid + ", Username: " + username + ", Email: " + email + ", Phone: " + phone, Toast.LENGTH_SHORT).show();
                        }

                        Log.d(TAG, "onResponse: Current User - Code: " + code + ", Message: " + message);
                    } else {
                        Toast.makeText(getApplicationContext(), "Empty response body", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleUnAuthorisedResponse(response);
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d(TAG, "onResponse: Request failed with code: " + response.code() + " and error: " + errorBody);
                        Toast.makeText(getApplicationContext(), "Request failed with code: " + response.code() + " and error: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onResponse: Request failed with code: " + response.code() + " and message: " + response.message());
                        Toast.makeText(getApplicationContext(), "Request failed with code: " + response.code() + " and message: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserServerResponse> call, Throwable throwable) {
                Log.d(TAG, "onFailure: API call failed with error: " + throwable.getMessage());
                Toast.makeText(getApplicationContext(), "API call failed with error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    void deleteArchived(String projectUid){
        Call<ProjectServerResponse> deleteArchivedCall = myApiClient.deleteArchivedProject(projectUid,"Bearer " + BEARER_TOKEN);
        deleteArchivedCall.enqueue(new Callback<ProjectServerResponse>() {
            @Override
            public void onResponse(Call<ProjectServerResponse> call, Response<ProjectServerResponse> response) {
                if (response.isSuccessful()) {
                    ProjectServerResponse deleteResponse = response.body();
                    if (deleteResponse != null) {
                        Log.d(TAG, "onResponse: Code: " + deleteResponse.getCode());
                        Log.d(TAG, "onResponse: Message: " + deleteResponse.getMessage());

                        if (deleteResponse.getCode() == 0) {
                            // Project deleted successfully
                            // update recyclerview
                            Log.d(TAG, "Project deleted successfully");
                        } else {
                            // Project not found on archived
                            Log.d(TAG, "onResponse: Error message: " + deleteResponse.getMessage());
                        }
                    }
                } else {
                    handleUnAuthorisedResponse(response);
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
    void getArchivedProjectTasks(String projectUuid) {
        String token = "Bearer " + BEARER_TOKEN;
        Call<ProjectTaskServerResponse> call = myApiClient.getArchivedProjectTasks(projectUuid, token);
        call.enqueue(new Callback<ProjectTaskServerResponse>() {
            @Override
            public void onResponse(Call<ProjectTaskServerResponse> call, Response<ProjectTaskServerResponse> response) {
                if (response.isSuccessful()) {
                    ProjectTaskServerResponse taskListResponse = response.body();
                    if (taskListResponse != null && taskListResponse.getData() != null) {
                        for (ProjectTask task : taskListResponse.getData()) {
                            Log.d(TAG, "onResponse: Task UUID: " + task.getUuid());
                            Log.d(TAG, "onResponse: Task Name: " + task.getName());
                            Log.d(TAG, "onResponse: Task Deadline: " + task.getDeadline());
                            Log.d(TAG, "onResponse: Task Created At: " + task.getCreatedAt());
                            Log.d(TAG, "onResponse: Task Project UUID: " + task.getProjectUuid());
                            Log.d(TAG, "onResponse: Task Archived At Time: " + task.getArchivedAt().getTime());
                            Log.d(TAG, "onResponse: Task Archived At Valid: " + task.getArchivedAt().isValid());
                            Log.d(TAG, "onResponse: Task Project Name: " + task.getProjectName());
                            // TODO: Update RecyclerView or UI component
                        }
                    } else {
                        Log.d(TAG, "onResponse: No tasks found or task list is empty");
                    }
                } else {
                    Log.d(TAG, "onResponse: Request failed with code: " + response.code() + " and message: " + response.message());
                    handleUnAuthorisedResponse(response);
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d(TAG, "onResponse: Error body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectTaskServerResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: API call failed with error: " + t.getMessage());
            }
        });
    }
    void deleteTask(String taskId) {
        String token = "Bearer " + BEARER_TOKEN;

        Call<ProjectTaskServerResponse> deleteTaskCall = myApiClient.deleteTask(taskId, token);
        deleteTaskCall.enqueue(new Callback<ProjectTaskServerResponse>() {
            @Override
            public void onResponse(Call<ProjectTaskServerResponse> call, Response<ProjectTaskServerResponse> response) {
                if (response.isSuccessful()) {
                    ProjectTaskServerResponse deleteResponse = response.body();
                    if (deleteResponse != null) {
                        Log.d(TAG, "onResponse: Code: " + deleteResponse.getCode());
                        Log.d(TAG, "onResponse: Message: " + deleteResponse.getMessage());

                        if (deleteResponse.getCode() == 0) {
                            // Archived Task deleted successfully
                            Log.d(TAG, "Task deleted successfully");
                        } else {
                            // Handle errors
                            Log.d(TAG, "onResponse: Error message: " + deleteResponse.getMessage());
                        }
                    }
                } else {
                    handleUnAuthorisedResponse(response);
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
            public void onFailure(Call<ProjectTaskServerResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: API call failed with error: " + t.getMessage());
            }
        });
    }
    void unarchiveProjectTask(String taskId) {
        String token = "Bearer " + BEARER_TOKEN;

        Call<ProjectTaskServerResponse> unarchiveTaskCall = myApiClient.unarchiveProjectTask(taskId, token);
        unarchiveTaskCall.enqueue(new Callback<ProjectTaskServerResponse>() {
            @Override
            public void onResponse(Call<ProjectTaskServerResponse> call, Response<ProjectTaskServerResponse> response) {
                if (response.isSuccessful()) {
                    ProjectTaskServerResponse unarchiveResponse = response.body();
                    if (unarchiveResponse != null) {
                        Log.d(TAG, "onResponse: Code: " + unarchiveResponse.getCode());
                        Log.d(TAG, "onResponse: Message: " + unarchiveResponse.getMessage());

                        if (unarchiveResponse.getCode() == 0) {
                            // Task unarchived successfully
                            Log.d(TAG, "Task unarchived successfully");
                        } else {
                            // Handle errors
                            Log.d(TAG, "onResponse: Error message: " + unarchiveResponse.getMessage());
                        }
                    }
                } else {
                    handleUnAuthorisedResponse(response);
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
            public void onFailure(Call<ProjectTaskServerResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: API call failed with error: " + t.getMessage());
            }
        });
    }
    @Override
    public void handleUnAuthorisedResponse(Response<?> response) {
        if (response.code() == 401) {
            Log.d(TAG, "handleUnAuthorisedResponse: ayam triggered");
                    FetchNewToken.getToken(MainActivity.this, new FetchNewToken.TokenCallback() {
                        @Override
                        public void onTokenReceived(String token) {
                            Log.d(TAG, "handleUnAuthorisedResponse: ayam triggered"+token);
                            BEARER_TOKEN = token;
                            getSharedPreferences("Access_Token",MODE_PRIVATE).edit().putString("token",token).apply();
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Swipe down to refresh", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onError(String errorMessage) {
                            // Handle the error
                            Log.d("MainActivity", "Error: " + errorMessage);
                            Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
        }


    @Override
    public void onProjectItemClick(String projectUid, String userUid, String name, String description, String createdTime) {
        Intent intent=new Intent(MainActivity.this, NoteDetails.class);
        intent.putExtra("projectUid",projectUid);
        intent.putExtra("UserUid",userUid);
        intent.putExtra("name",name);
        intent.putExtra("description",description);
        intent.putExtra("createdTime",createdTime);
        startActivity(intent);
    }

    @Override
    public void onTaskItemClick(String projectUid, String userUid, String name, String description, String createdTime) {

    }

}