package com.example.root.meeting.Services;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by root on 16.07.18.
 */

public class MessageService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("fLog","from:"+ remoteMessage.getFrom());

        if (remoteMessage.getData().size()>0){
            Log.d("fLog","message"+remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            Log.d("fLog", "Message Notification body: " + remoteMessage.getNotification().getBody());
        }

    }
}
