package com.example.root.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.root.meeting.ObRealm.Meeting;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 30.04.18.
 */

public class CreateMeeting extends AppCompatActivity {
    private int id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_meeting_activity);
    }
    public void createMeeting (View view) throws InterruptedException {
        Meeting meeting = new Meeting();
        String st = ((EditText) findViewById(R.id.meetingName)).getText().toString();
        if (st != null) {
            if (st.length() > 0) {
                meeting.setName(st);
                App.getApi().createMeeting(MainActivity.getAuthToken(), meeting).enqueue(new Callback<Meeting>() {
                    @Override
                    public void onResponse(Call<Meeting> call, Response<Meeting> response) {
                        if (response.isSuccessful()) {
                            id = response.body().getId();
                            startActivity(new Intent(CreateMeeting.this,MeetingActivity.class).putExtra("id", id));
                        }
                    }

                    @Override
                    public void onFailure(Call<Meeting> call, Throwable t) {
                        Toast.makeText(CreateMeeting.this, "error" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "need name", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
