package com.example.root.meeting.apis;

import android.app.Application;

import com.example.root.meeting.apis.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class App extends Application {
    private static Api api;
    private Retrofit retrofit;
    private static String adress_mai="http://172.31.66.61:8080/";
    private static String adress_phone="http://192.168.43.246:8080/";
    private static String adress_phone_2 = "http://192.168.43.246:8080/";
    private static String adress_home = "http://192.168.2.98:8080/";

    @Override
    public void onCreate() {
        super.onCreate();
        retrofit = new Retrofit.Builder()
                .baseUrl(adress_mai)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
                api = retrofit.create(Api.class);

    }
    public static Api getApi(){
        return api;
    }

}
