package com.example.root.meeting.Meeting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.meeting.FindUser;
import com.example.root.meeting.MainActivity;
import com.example.root.meeting.ObRealm.Meeting;
import com.example.root.meeting.ObRealm.MessagingData;
import com.example.root.meeting.ObRealm.User;
import com.example.root.meeting.R;
import com.example.root.meeting.apis.App;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private Gson gson = new Gson();
    private double latitude, longitude;
    private ArrayAdapter<User> adapter;
    private List<User> users = new ArrayList<>();

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_meeting_activity);
        sharedPreferences = getSharedPreferences("UserKeys",MODE_PRIVATE);
        ListView lvMain = (ListView) findViewById(R.id.usersList);
        adapter = new ArrayAdapter<User>(this,
                android.R.layout.simple_list_item_1,android.R.id.text1, users){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final User user = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(user.getUsername());
                return view;
            }
        };
        lvMain.setAdapter(adapter);
    }
    public void changePlace(View view){
        startActivityForResult(new Intent(CreateMeeting.this,ChangePlace.class),2);
    }
    public void findUser(View view){
        startActivityForResult(new Intent(CreateMeeting.this, FindUser.class),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                User user = gson.fromJson(data.getStringExtra("user"),User.class);
                users.add(user);
                adapter.notifyDataSetChanged();
            case 2:
                latitude=data.getDoubleExtra("latitude",0);
                longitude=data.getDoubleExtra("longitude",0);
                break;
        }
    }

    public void createMeeting (View view){
        String st = ((EditText) findViewById(R.id.meetingName)).getText().toString();
        if (st != null) {
            if (st.length() > 0) {
                Meeting meeting = new Meeting();
                meeting.setName(st);
                meeting.setAdmin(sharedPreferences.getString("mail",""));
                meeting.addAllUsers(users);
                meeting.setLatitude(latitude);
                meeting.setLongitude(longitude);
                Log.d("MeetingLogs",gson.toJson(meeting));
                App.getApi().createMeeting(MainActivity.getAuthToken(), meeting).enqueue(new Callback<Meeting>() {
                    @Override
                    public void onResponse(Call<Meeting> call, Response<Meeting> response) {
                        if (response.isSuccessful()) {
                                if (response.body()!=null) {
                                    startActivity(new Intent(CreateMeeting.this, MeetingActivity.class).putExtra("meeting", gson.toJson(response.body())));
                                    finish();
                                }
                        }else{
                            Toast.makeText(CreateMeeting.this,"something gone wrong",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Meeting> call, Throwable t) {
                        Toast.makeText(CreateMeeting.this, "error" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("CreateMeeting",t.getLocalizedMessage());
                    }
                });
            } else {
                Toast.makeText(this, "need name", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
