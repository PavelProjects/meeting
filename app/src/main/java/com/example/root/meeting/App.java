package com.example.root.meeting;

import android.app.Application;

import com.example.root.meeting.apis.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class App extends Application {
    private static Api api;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.246:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
                api = retrofit.create(Api.class);

    }
    public static Api getApi(){
        return api;
    }

}
