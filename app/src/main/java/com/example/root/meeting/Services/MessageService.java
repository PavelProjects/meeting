package com.example.root.meeting.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.root.meeting.MainActivity;
import com.example.root.meeting.Meeting.MeetingActivity;
import com.example.root.meeting.ObRealm.Meeting;
import com.example.root.meeting.ObRealm.User;
import com.example.root.meeting.Profile.UserProfile;
import com.example.root.meeting.R;
import com.example.root.meeting.apis.App;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 16.07.18.
 */

public class MessageService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("fLog","from:"+ remoteMessage.getFrom());

        if (remoteMessage.getData().size()>0){
            Log.d("fLog",remoteMessage.getData().toString());
        }
        showNotifi(remoteMessage);
    }
    private void showNotifi(RemoteMessage remoteMessage){
        int id = 0;
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setColorized(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setVibrate(new long[] { 1000, 100, 1000});

        if (remoteMessage.getData().get("event").equals("add_to_meeting")){
            Intent resultIntent = new Intent(this,MeetingActivity.class).putExtra("meeting",remoteMessage.getData().get("info"));
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentInfo(remoteMessage.getData().get("info"));
            builder.setContentTitle("Meetings");
            builder.setContentText("You have been invited to the meeting!");
            builder.setContentIntent(resultPendingIntent);
            id= 1;
        }
        if (remoteMessage.getData().get("event").equals("add_friend")){
            Intent resultIntent = new Intent(this,UserProfile.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            User user = new Gson().fromJson(remoteMessage.getData().get("user"),User.class);
            builder.setContentTitle("new friend!");
            builder.setContentText(user.getUsername()+" added you as a friend!");
            builder.setContentIntent(resultPendingIntent);
            id = 2;
        }

        if (remoteMessage.getData().get("event").equals("update_meeting")){
            sendBroadcast(new Intent().setAction(MeetingActivity.ACTION_UPDATE));
        }
        if (remoteMessage.getData().get("event").equals("message")){
            builder.setContentText("message!");
            Meeting meeting= new Gson().fromJson(remoteMessage.getData().get("meeting"),Meeting.class);
            Intent intent =new Intent();
            intent.putExtra("id",meeting.getId());
            intent.putExtra("message",remoteMessage.getData().get("message"));
            sendBroadcast(intent);
        }
        if (id!=0) {
            Notification notification = builder.build();
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(id, notification);
        }
    }
    @Override
    public void onNewToken(String token) {
        Log.d("fLog", "Refreshed token: " + token);
        SharedPreferences.Editor editor;
        SharedPreferences userKeys=getSharedPreferences("UserKeys",MODE_PRIVATE);
        editor = userKeys.edit();
        editor.putString("id",token);
        editor.apply();
        App.getApi().updateToken(MainActivity.getAuthToken(),token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
