package com.example.root.meeting.Meeting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.root.meeting.MainActivity;
import com.example.root.meeting.ObRealm.Meeting;
import com.example.root.meeting.R;
import com.example.root.meeting.apis.App;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 30.04.18.
 */

public class CreateMeeting extends AppCompatActivity {
    private int id;
    private boolean flag;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_meeting_activity);
        sharedPreferences = getSharedPreferences("UserKeys",MODE_PRIVATE);
    }
    public void createMeeting (View view){
        String st = ((EditText) findViewById(R.id.meetingName)).getText().toString();
        String adr=((EditText) findViewById(R.id.meetingAdress)).getText().toString();
        if (st != null && adr!=null) {
            if (st.length() > 0 && adr.length()>0) {
                Meeting meeting = new Meeting();
                //flag = false;
                meeting.setName(st);
                meeting.setAdress(adr);
                meeting.setAdmin(sharedPreferences.getString("mail",""));
                App.getApi().createMeeting(MainActivity.getAuthToken(), meeting).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (response.isSuccessful()) {
                            if (response.isSuccessful()) {
                                id = response.body();
                                flag = true;
                                startActivity(new Intent(CreateMeeting.this, MeetingActivity.class).putExtra("id", id));
                                finish();
                            }
                        }else{
                            Toast.makeText(CreateMeeting.this,"something gone wrong",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Toast.makeText(CreateMeeting.this, "error" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "need name", Toast.LENGTH_SHORT).show();
            }
        }
//        if (flag){
//            startActivity(new Intent(CreateMeeting.this,MeetingActivity.class).putExtra("id", id));
//            finish();
//        }
    }
}
