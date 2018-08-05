package com.example.root.meeting.apis;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by root on 23.07.18.
 */

public interface FirebaseApi {
    @POST("/messages:send")
    Call<Void> sendMessage(@Header("Authorization") String token,String message);
}
