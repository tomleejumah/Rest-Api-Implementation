package com.app.postmancollection;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.postmancollection.Adapters.TaskAdapter;
import com.app.postmancollection.Api.MyApiClient;
import com.app.postmancollection.Inteface.OnItemClickListener;
import com.app.postmancollection.Model.ProjectModels.Project;
import com.app.postmancollection.Model.ProjectModels.ProjectServerResponse;
import com.app.postmancollection.Model.ProjectTaskModels.ProjectTask;
import com.app.postmancollection.Model.ProjectTaskModels.ProjectTaskServerResponse;
import com.app.postmancollection.Model.ProjectTaskModels.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteDetails extends AppCompatActivity implements OnItemClickListener {
    Button delete_note_text_view_btn, btnUpdate;
    TextView noteTime,page_title;
    EditText notes_title_text, notes_content_text;
    private MyApiClient myApiClient;
    private String BEARER_TOKEN;
    String projectUid;
    private static final String TAG = "NoteDetails";
    private ConstraintLayout constraintLayout;
    String pickedDate;
    TextView txtDeadline;
    EditText taskName;
    List<ProjectTask> projectTaskList;
    Dialog dialog;
    TaskAdapter taskAdapter;
    String ActionType = null;
    String rawName,rawUid;
    String rawCreatedTime;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        myApiClient = MyApp.getMyApiClient();
        BEARER_TOKEN = getSharedPreferences("Access_Token", MODE_PRIVATE).getString("token", null);


        delete_note_text_view_btn = findViewById(R.id.delete_note_text_view_btn);
        noteTime = findViewById(R.id.noteTime);
        btnUpdate = findViewById(R.id.btnUpdate);
        notes_title_text = findViewById(R.id.notes_title_text);
        notes_content_text = findViewById(R.id.notes_content_text);
        page_title = findViewById(R.id.page_title);
        constraintLayout = findViewById(R.id.pp);



        Intent intent = getIntent();
        String action = intent.getStringExtra("action");

        if ("create".equals(action)) {
            constraintLayout.setVisibility(View.GONE);
            delete_note_text_view_btn.setVisibility(View.GONE);
            page_title.setText("Create Note");
            btnUpdate.setText("Save");
            btnUpdate.setTag("Save");
            noteTime.setVisibility(View.GONE);

        } else {
//            delete_note_text_view_btn.setVisibility(View.VISIBLE);
            page_title.setText("Update Note");
            noteTime.setVisibility(View.VISIBLE);
            constraintLayout.setVisibility(View.VISIBLE);
            btnUpdate.setText("Update");
            btnUpdate.setTag("Update");

            projectUid = intent.getStringExtra("projectUid");
            getProjectTask(projectUid);
            String userUid = intent.getStringExtra("UserUid");
            String name = intent.getStringExtra("name");
            String description = intent.getStringExtra("description");
            String createdTime = intent.getStringExtra("createdTime");

            notes_content_text.setText(description);
            notes_title_text.setText(name);
            noteTime.setText("Time Created\n" + createdTime);
        }

        RecyclerView rcMoreJobs = findViewById(R.id.rcTasks);
        projectTaskList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(NoteDetails.this, LinearLayoutManager.HORIZONTAL, false);
        rcMoreJobs.setLayoutManager(layoutManager);
        Collections.shuffle(projectTaskList);
         taskAdapter = new TaskAdapter(false, projectTaskList, NoteDetails.this,this);
        rcMoreJobs.setAdapter(taskAdapter);


        findViewById(R.id.btnUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();
                if (tag != null && tag.equals("Update")) {
                    updateProject(projectUid, notes_title_text.getText().toString(), notes_content_text.getText().toString());
                } else {
                    addProject(notes_title_text.getText().toString(), notes_content_text.getText().toString());
                }
            }
        });
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionType  ="Create";
               showTaskDialog();
            }
        });
    }

//    action update
//    action add
    private void showTaskDialog() {
        dialog = new Dialog(NoteDetails.this);

        dialog.setContentView(R.layout.add_task_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg));
        dialog.setCancelable(true);

        dialog.show();

        taskName = dialog.findViewById(R.id.taskName);
        txtDeadline = dialog.findViewById(R.id.txtDeadline);

        if (ActionType.equals("Update")) {
            taskName.setText(rawName);
            txtDeadline.setText("Deadline : " + rawCreatedTime);
        }

        txtDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActionType.equals("Update")) {
                    taskName.setText(rawName);
                    txtDeadline.setText("Deadline: " + rawCreatedTime);
                    if(pickedDate == null){
                        pickedDate = rawCreatedTime;
                    }
                    Log.d(TAG, "onClick: "+rawUid+ " "+taskName.getText().toString()+" "+pickedDate);
                    updateTask(rawUid, taskName.getText().toString(), pickedDate);
                } else if (ActionType.equals("Create"))
                    createTask(projectUid, taskName.getText().toString(), pickedDate);
            }
        });
    }

    private void showDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and show it
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                NoteDetails.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Set the selected date into the calendar
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Format the date and display it
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String formattedDate = sdf.format(calendar.getTime());
//                        textViewDate.setText(formattedDate);
                        pickedDate = formattedDate;
                        txtDeadline.setText("Deadline: " + pickedDate);
//                        Toast.makeText(NoteDetails.this, "Selected Date: " + formattedDate, Toast.LENGTH_SHORT).show();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    public void addProject(String newName, String newDescription) {
        Project project = new Project();
        project.setName(newName);
        project.setDescription(newDescription);

        Call<ProjectServerResponse> createProject = myApiClient.createProject("Bearer " + BEARER_TOKEN, project);
        createProject.enqueue(new Callback<ProjectServerResponse>() {
            @Override
            public void onResponse(Call<ProjectServerResponse> call, Response<ProjectServerResponse> response) {
                if (response.isSuccessful()) {
                    ProjectServerResponse projectServerResponse = response.body();
                    Log.d(TAG, "onResponse: Project created : " + projectServerResponse);
                    finish();
                    Toast.makeText(NoteDetails.this, "Project created", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d(TAG, "onResponse: Request failed with code: " + response.code() + " and error: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onResponse: Request failed with code: " + response.code() + " and message: " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectServerResponse> call, Throwable throwable) {

            }
        });
    }

    void updateProject(String projectUuid, String newName, String newDescription) {
        Project project = new Project();
        project.setName(newName);
        project.setDescription(newDescription);
        // Set other fields if necessary

        String token = "Bearer " + BEARER_TOKEN;
        Call<ProjectServerResponse> updateProjectCall = myApiClient.updateProject(projectUuid, token, project);

        updateProjectCall.enqueue(new Callback<ProjectServerResponse>() {
            @Override
            public void onResponse(Call<ProjectServerResponse> call, Response<ProjectServerResponse> response) {
                if (response.isSuccessful()) {
                    ProjectServerResponse updateResponse = response.body();
                    if (updateResponse != null) {
                        Log.d(TAG, "onResponse: Code: " + updateResponse.getCode());
                        Log.d(TAG, "onResponse: Message: " + updateResponse.getMessage());

                        if (updateResponse.getCode() == 0) {
                            // Project updated successfully
                            Log.d(TAG, "Project updated successfully");
                            finish();
                            Toast.makeText(NoteDetails.this, "Project updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Update failed
                            Log.d(TAG, "onResponse: Error message: " + updateResponse.getMessage());
                        }
                    }
                } else {
//                    handleUnAuthorisedResponse(response);
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

    void createTask(String projectUuid, String name, String deadline) {
        Task task = new Task();
        task.setProjectUuid(projectUuid);
        task.setName(name);
        task.setDeadline(deadline);

        Call<ProjectTaskServerResponse> updateTaskCall = myApiClient.createTask("Bearer " + BEARER_TOKEN, task);
        updateTaskCall.enqueue(new Callback<ProjectTaskServerResponse>() {
            @Override
            public void onResponse(Call<ProjectTaskServerResponse> call, Response<ProjectTaskServerResponse> response) {
                if (response.isSuccessful()) {
                    ProjectTaskServerResponse taskResponse = response.body();
                    if (taskResponse != null) {
                        Log.d(TAG, "onResponse: Code: " + taskResponse.getCode());
                        Log.d(TAG, "onResponse: Message: " + taskResponse.getMessage());

                        if (taskResponse.getCode() == 0) {
                            // Task updated successfully
                            Toast.makeText(NoteDetails.this, "Task created successfully", Toast.LENGTH_SHORT).show();
                            getProjectTask(projectUuid);
                            taskAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        } else {
                            // Handle errors
                            Log.d(TAG, "onResponse: Error message: " + taskResponse.getMessage());
                        }
                    }
                } else {
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

    void getProjectTask(String projectUid) {
//        String token = "Bearer " + BEARER_TOKEN;
        Call<ProjectTaskServerResponse> getTaskCall = myApiClient.getProjectTask(projectUid, "Bearer " + BEARER_TOKEN);
        getTaskCall.enqueue(new Callback<ProjectTaskServerResponse>() {
            @Override
            public void onResponse(Call<ProjectTaskServerResponse> call, Response<ProjectTaskServerResponse> response) {
                if (response.isSuccessful()) {
                    projectTaskList.clear();
                    ProjectTaskServerResponse taskListResponse = response.body();
                    if (taskListResponse != null) {
                        Log.d(TAG, "onResponse: Code: " + taskListResponse.getCode());
                        Log.d(TAG, "onResponse: Message: " + taskListResponse.getMessage());
                        List<ProjectTask> tasks = taskListResponse.getData();
                        if (tasks != null && !tasks.isEmpty()) {
                            ProjectTask task = tasks.get(0);
                            projectTaskList.addAll(tasks);
                            taskAdapter.notifyDataSetChanged();

                        }
                    }
                } else {
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

    void updateTask(String taskId, String name, String deadline) {
        Task taskUpdateRequest = new Task(name, deadline);
        Call<ProjectTaskServerResponse> updateTaskCall = myApiClient.updateTask(taskId, "Bearer " + BEARER_TOKEN, taskUpdateRequest);
        updateTaskCall.enqueue(new Callback<ProjectTaskServerResponse>() {
            @Override
            public void onResponse(Call<ProjectTaskServerResponse> call, Response<ProjectTaskServerResponse> response) {
                if (response.isSuccessful()) {
                    ProjectTaskServerResponse taskResponse = response.body();
                    if (taskResponse != null) {
                        Log.d(TAG, "onResponse: Code: " + taskResponse.getCode());
                        Log.d(TAG, "onResponse: Message: " + taskResponse.getMessage());

                        if (taskResponse.getCode() == 0) {
                            // Task updated successfully
                            dialog.dismiss();
                            Toast.makeText(NoteDetails.this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Task updated successfully");
                        } else {
                            // Handle errors
                            Log.d(TAG, "onResponse: Error message: " + taskResponse.getMessage());
                        }
                    }
                } else {
//                    handleUnAuthorisedResponse(response);
                    dialog.dismiss();
                    Toast.makeText(NoteDetails.this, "Task updated failed", Toast.LENGTH_SHORT).show();
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
    public void onProjectItemClick(String projectUid, String userUid, String name, String description, String createdTime) {

    }


    @Override
    public void onTaskItemClick(String projectUid, String userUid, String name, String description, String createdTime) {
        ActionType  ="Update";
        rawName = name;
        rawCreatedTime = createdTime;
        rawUid = projectUid;
        showTaskDialog();

    }
}