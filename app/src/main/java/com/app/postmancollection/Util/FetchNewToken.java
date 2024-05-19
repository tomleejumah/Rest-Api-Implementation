package com.app.postmancollection.Util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.app.postmancollection.Api.MyApiClient;
import com.app.postmancollection.Api.MyApiService;
import com.app.postmancollection.Model.UserModels.UserCredentials;
import com.app.postmancollection.Model.UserModels.UserServerResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchNewToken {
    private static final String TAG = "FetchNewToken";

    public static void getToken(Context context, TokenCallback callback) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        String email = sharedPreferences.getString("email", null);
        String password = sharedPreferences.getString("password", null);

        if (email != null && password != null) {
            MyApiClient myApiClient = MyApiService.getClient().create(MyApiClient.class);

            UserCredentials userCredentials = new UserCredentials();
            userCredentials.setUsernameOrEmail(email);
            userCredentials.setPassword(password);

            Call<UserServerResponse> call = myApiClient.loginUser(userCredentials);
            call.enqueue(new Callback<UserServerResponse>() {
                @Override
                public void onResponse(Call<UserServerResponse> call, Response<UserServerResponse> response) {
                    if (response.isSuccessful()) {
                        UserServerResponse userServerResponse = response.body();
                        if (userServerResponse != null && userServerResponse.getCode() == 0) {
                            UserCredentials data = userServerResponse.getData();
                            String token = data != null ? data.getAccessToken() : null;
                            if (token != null) {
                                callback.onTokenReceived(token);
                            } else {
                                callback.onError("Token is null");
                            }
                        } else {
                            callback.onError("Login failed: " + (userServerResponse != null ? userServerResponse.getMessage() : "Unknown error"));
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            callback.onError("Request failed with code: " + response.code() + " and error: " + errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                            callback.onError("Request failed with code: " + response.code() + " and message: " + response.message());
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserServerResponse> call, Throwable t) {
                    callback.onError(t.getMessage());
                }
            });
        } else {
            callback.onError("Email or password is null");
        }
    }

    public interface TokenCallback {
        void onTokenReceived(String token);
        void onError(String errorMessage);
    }
}
