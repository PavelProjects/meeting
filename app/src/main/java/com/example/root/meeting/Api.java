package com.example.root.meeting;

import com.example.root.meeting.ObRealm.Meeting;
import com.example.root.meeting.ObRealm.User;

import org.json.JSONArray;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by root on 11.02.18.
 */

public interface Api {
    @GET("/crud-1.0.0-SNAPSHOT/users/")
    Call<List<User>> getUsers(@Header("Authorization") String authkey);
    @GET("/crud-1.0.0-SNAPSHOT/users/{id}")
    Call<List<User>> getUserById(@Header("Authorization") String authkey, @Path("id") int id);
    @GET("/crud-1.0.0-SNAPSHOT/meeting")
    Call<List<Meeting>> getMeetings( @Header("Authorization") String authkey);
    @GET("/crud-1.0.0-SNAPSHOT/meeting/{id}")
    Call<Meeting> getMeeting(@Header("Authorization") String authkey, @Path("id") int id);
    @POST("/crud-1.0.0-SNAPSHOT/meeting/{id}/{uid}")
    Call<ResponseBody> addUserToMeeting(@Header("Authorization") String authkey,@Path("id") int id,@Path("uid") int uid);
    @GET("/crud-1.0.0-SNAPSHOT/users/fname/{name}")
    Call<List<User>> getUserByName(@Header("Authorization") String authkey, @Path("name") String name);
    @DELETE("/crud-1.0.0-SNAPSHOT/meeting/{id}/{uid}")
    Call<ResponseBody> deleteUserFromMeeting(@Header("Authorization") String authkey,@Path("id") int id,@Path("uid") int uid);
}
