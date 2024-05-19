package com.app.postmancollection;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.postmancollection.Api.MyApiClient;
import com.app.postmancollection.Model.AuthErrorDetails;
import com.app.postmancollection.Model.AuthServerErrorResponse;
import com.app.postmancollection.Model.UserModels.UserCredentials;
import com.app.postmancollection.Model.UserModels.UserServerResponse;
import com.app.postmancollection.Util.NetworkUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextInputEditText edtMail, edtPwsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtMail=findViewById(R.id.edtMail);
        edtPwsd=findViewById(R.id.edtPassword);

        findViewById(R.id.btnLogin).setOnClickListener(v->{
            if (NetworkUtils.isNetworkConnected(this)) {
                    loginUser(String.valueOf(edtMail.getText()), String.valueOf(edtPwsd.getText()));
            }else{
                    Toast.makeText(this, "No internet Connection", Toast.LENGTH_SHORT).show();
                }
        });
    }
    void loginUser(String usernameOrEmail, String password) {

        MyApiClient myApiClient = MyApp.getMyApiClient();

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setUsernameOrEmail(usernameOrEmail);
        userCredentials.setPassword(password);

        Call<UserServerResponse> call = myApiClient.loginUser(userCredentials);
        call.enqueue(new Callback<UserServerResponse>() {
            @Override
            public void onResponse(Call<UserServerResponse> call, Response<UserServerResponse> response) {
                if (response.isSuccessful()) {
                    UserServerResponse userServerResponse = response.body();
                    if (userServerResponse != null) {
                        int code = userServerResponse.getCode();
                        String message = userServerResponse.getMessage();
                        UserCredentials data = userServerResponse.getData();

                        if (code == 0) {
                            // Login successful, proceeding to next page
                            String token = data != null ? data.getAccessToken() : null;
                            if (token != null) {
                                Log.d(TAG, "onResponse: Access token: " + token);

//                                update bearer/token also login info in shared ref for future fetch
                                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                //saving the token so as to use it for other calls
                                getSharedPreferences("Access_Token",MODE_PRIVATE).edit().putString("token",token).apply();

                                // only saving for testing purpose so as not to log in everytime
                                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("token", token);

                                String email = edtMail.getText().toString();
                                String password = edtPwsd.getText().toString();
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.apply();

                            } else {
                                Log.d(TAG, "onResponse: Access token is null");
                                Toast.makeText(LoginActivity.this, "Your Access Token Cant be generated", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(getApplicationContext(), "WELCOME BACK", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMessage = message != null ? message : "Unknown error";
                            Toast.makeText(getApplicationContext(), "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Empty response body", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        //Setting up error for respective EditTexts if inputs are not right
                        String errorBody = response.errorBody().string();
                        Gson gson = new Gson();
                        AuthServerErrorResponse errorResponse = gson.fromJson(errorBody, AuthServerErrorResponse.class);
                        if (errorResponse != null) {
                            String message = errorResponse.getMessage();
                            AuthErrorDetails errorDetails = errorResponse.getData();

                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            TextInputEditText[] editTexts = new TextInputEditText[]{edtMail, edtPwsd};

                            if (errorDetails != null) {
                                List<String> usernameOrEmailErrors = errorDetails.getUsernameOrEmail();
                                List<String> passwordErrors = errorDetails.getPassword();

                                if (usernameOrEmailErrors != null && !usernameOrEmailErrors.isEmpty()) {
                                    editTexts[0].setError(usernameOrEmailErrors.get(0));
                                }
                                if (passwordErrors != null && !passwordErrors.isEmpty()) {
                                    editTexts[1].setError(passwordErrors.get(0));
                                }
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onResponse: Request failed with code: " + response.code() + " and message: " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserServerResponse> call, Throwable throwable) {
                Toast.makeText(getApplicationContext(),"Api cant be reached "+throwable,Toast.LENGTH_SHORT).show();
            }
        });

    }
}