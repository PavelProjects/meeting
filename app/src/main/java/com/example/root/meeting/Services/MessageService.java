package com.example.root.meeting.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.root.meeting.MainActivity;
import com.example.root.meeting.Meeting.MeetingActivity;
import com.example.root.meeting.Meeting.StartMeetingActivity;
import com.example.root.meeting.ObRealm.Meeting;
import com.example.root.meeting.ObRealm.MessagingData;
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
    private Gson gson = new Gson();

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
        Intent resultIntent;
        PendingIntent resultPendingIntent;
        Intent intent;
        switch (remoteMessage.getData().get("event")){
            case "add_to_meeting" :
                resultIntent = new Intent(this,StartMeetingActivity.class);
                resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentInfo(remoteMessage.getData().get("info"));
                builder.setContentTitle("Meetings");
                builder.setContentText("You have been invited to the meeting!");
                builder.setContentIntent(resultPendingIntent);
                intent =new Intent(StartMeetingActivity.ACTION_UPDATE);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                id= 1;
                break;
            case "add_friend" :
                resultIntent = new Intent(this,UserProfile.class);
                resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                User user = new Gson().fromJson(remoteMessage.getData().get("user"),User.class);
                builder.setContentTitle("new friend!");
                builder.setContentText(user.getUsername()+" added you as a friend!");
                builder.setContentIntent(resultPendingIntent);
                id = 2;
                break;
            case "message" :
                int mid= Integer.valueOf(remoteMessage.getData().get("mid"));
                intent =new Intent(MeetingActivity.ACTION_NEW_MESSAGE);
                intent.putExtra("id",mid);
                intent.putExtra("message",remoteMessage.getData().toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                if (MeetingActivity.isActivityVisible()){
                    id =0;
                }else {
                    resultIntent = new Intent(this,StartMeetingActivity.class);
                    resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentTitle("MESSAGE");
                    builder.setContentText(remoteMessage.getData().get("message"));
                    builder.setContentIntent(resultPendingIntent);
                    id = 3;
                }
                break;
            case "delete_from_meeting" :
                intent =new Intent(MeetingActivity.DELETE_FROM_MEETING);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                break;
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
