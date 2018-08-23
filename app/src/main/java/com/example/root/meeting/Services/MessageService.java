package com.example.root.meeting.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.root.meeting.MainActivity;
import com.example.root.meeting.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by root on 16.07.18.
 */

public class MessageService extends FirebaseMessagingService {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("fLog","from:"+ remoteMessage.getFrom());

        if (remoteMessage.getData().size()>0){
            Log.d("fLog",remoteMessage.getData().get("f")+" : "+remoteMessage.getData().get("message"));

        }
        if (remoteMessage.getNotification() != null) {
            Log.d("fLog", "Message Notification body: " + remoteMessage.getNotification().getBody());
        }
    }
}
