package com.example.root.meeting;

import android.app.Application;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class App extends Application {
    private static Api api;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
                retrofit = new Retrofit.Builder()
                        .baseUrl("http://192.168.2.245:8080/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                api = retrofit.create(Api.class);
    }
    public static Api getApi(){
        return api;
    }
}
