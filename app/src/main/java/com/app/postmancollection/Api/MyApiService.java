package com.app.postmancollection.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApiService {
    private static final String BASE_URL = "https://mngmtapp.malakoff.co/api/v1/";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public static Retrofit getClient() {
        return retrofit;
    }

}
