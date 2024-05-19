package com.app.postmancollection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private TextInputEditText edtUserName, edtEmail, edtPhone, edtPassword, edtPassword1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         edtUserName = findViewById(R.id.edtUserName);
         edtEmail = findViewById(R.id.edtMail);
         edtPhone = findViewById(R.id.edtPhoneNo);
         edtPassword = findViewById(R.id.edtPassword);
         edtPassword1 = findViewById(R.id.edtPassword1);

        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(edtUserName.getText().toString(), edtEmail.getText().toString(),
                        edtPhone.getText().toString(), edtPassword.getText().toString(), edtPassword1.getText().toString());
            }
        });
    }

    void registerUser(String userName, String email, String phone, String password, String confirmPassword) {
        MyApiClient myApiClient = MyApp.getMyApiClient();

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setUsername(userName);
        userCredentials.setEmail(email);
        userCredentials.setPhone(phone);
        userCredentials.setPassword(password);
        userCredentials.setConfirmPassword(confirmPassword);

        Call<Void> call = myApiClient.registerUser(userCredentials);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    String errorMessage = "";
                    try {
                        errorMessage = response.errorBody().string();
                        String errorBody = response.errorBody().string();
                        Gson gson = new Gson();
                        AuthServerErrorResponse errorResponse = gson.fromJson(errorBody, AuthServerErrorResponse.class);

                        if (errorResponse != null) {
                            String message = errorResponse.getMessage();
                            AuthErrorDetails errorDetails = errorResponse.getData();

                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Registration failed with error: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Registration failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Registration failed with error: " + throwable.getMessage());
            }
        });
    }

}