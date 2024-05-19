package com.app.postmancollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.postmancollection.Api.MyApiClient;


public class StartScreen extends AppCompatActivity {
    private Button btn_register,btn_Login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_register = findViewById(R.id.btn_register);
        btn_Login = findViewById(R.id.btn_Login);

        String authTokenAccess = getSharedPreferences("Access_Token", MODE_PRIVATE).getString("token", null);

        if (authTokenAccess != null) {
            btn_register.setVisibility(View.GONE);
            btn_Login.setVisibility(View.GONE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(StartScreen.this, MainActivity.class));
                    finish();
                }
            }, 4000);

        }

        btn_register.setOnClickListener(v1 -> {
            startActivity(new Intent(StartScreen.this, RegisterActivity.class));
            finish();
        });
        btn_Login.setOnClickListener(v -> {
            startActivity(new Intent(StartScreen.this, LoginActivity.class));
            finish();
        });
    }
}