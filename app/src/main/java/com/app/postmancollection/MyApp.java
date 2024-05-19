package com.app.postmancollection;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.app.postmancollection.Api.MyApiClient;
import com.app.postmancollection.Api.MyApiService;

import retrofit2.Retrofit;

public class MyApp extends Application {
    private static MyApiClient myApiClient;
    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // Initialize Retrofit instance
        Retrofit retrofit = MyApiService.getClient();
        myApiClient = retrofit.create(MyApiClient.class);
    }
    public static MyApiClient getMyApiClient() {
        return myApiClient;
    }
}
