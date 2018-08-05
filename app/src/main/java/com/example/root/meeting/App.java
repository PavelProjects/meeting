package com.example.root.meeting;

import android.app.Application;

import com.example.root.meeting.apis.Api;
import com.example.root.meeting.apis.FirebaseApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class App extends Application {
    private static Api api;
    private static FirebaseApi firebaseApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.42.41:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
                api = retrofit.create(Api.class);

    }
    public static Api getApi(){
        return api;
    }

}
