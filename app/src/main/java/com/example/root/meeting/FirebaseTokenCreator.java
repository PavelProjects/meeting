package com.example.root.meeting;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by root on 24.07.18.
 */

public class FirebaseTokenCreator {

    public static String getAccessToken() throws IOException {
        GoogleCredential googleCredential = GoogleCredential
                .fromStream(new FileInputStream(""))
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));
        googleCredential.refreshToken();
        return googleCredential.getAccessToken();
    }
}
