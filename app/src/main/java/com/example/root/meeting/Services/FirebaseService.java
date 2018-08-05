package com.example.root.meeting.Services;



import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by root on 16.07.18.
 */

public class FirebaseService extends FirebaseInstanceIdService{
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("fLog", "TOKEN: " + refreshedToken);

    }
}
