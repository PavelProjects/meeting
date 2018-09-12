package com.example.root.meeting.Meeting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.meeting.MainActivity;
import com.example.root.meeting.ObRealm.Meeting;
import com.example.root.meeting.ObRealm.User;
import com.example.root.meeting.R;
import com.example.root.meeting.apis.App;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingActivity extends AppCompatActivity {
    private List<User> users  = new ArrayList<>();
    private ArrayAdapter<User> adapter;
    Meeting meeting =  new Meeting();
    private int pid = 0;
    public static String ACTION_UPDATE = "update";
    private MeetReceiver meetReceiver;
    Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_activity);

        Intent intent = getIntent();
        meeting = gson.fromJson(intent.getStringExtra("meeting"),Meeting.class);
        pid = meeting.getId();
        if (pid==0){
            finish();
        }
        ((TextView) findViewById(R.id.meetingTimeDate)).setText(String.valueOf(meeting.getDate()) + ":" + meeting.getTime());
        ((TextView) findViewById(R.id.meetingAddress)).setText(meeting.getAdress());
        users = meeting.getUsers();
        ListView lvMain = (ListView) findViewById(R.id.members_list);
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
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user =users.get(i);
                deleteUser(user.getId());
                users.remove(i);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        meetReceiver = new MeetReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE);
        registerReceiver(meetReceiver,intentFilter);
    }

    public void addUser(View view){
        startActivity(new Intent(MeetingActivity.this,AddUserToMeeting.class).putExtra("id",pid));
        finish();
    }
    private void deleteUser(String uid){
        App.getApi().deleteUserFromMeeting(MainActivity.getAuthToken(),pid,uid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    public void getMeeting(final int id) {
            App.getApi().getMeeting(MainActivity.getAuthToken(), id).enqueue(new Callback<Meeting>() {
                @Override
                public void onResponse(Call<Meeting> call, Response<Meeting> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            meeting.setName(response.body().getName());
                            meeting.setDate(response.body().getDate());
                            meeting.setTime(response.body().getTime());
                            meeting.setAdress(response.body().getAdress());
                        } else {
                            getMeeting(id);
                        }
                    } else {
                        Toast.makeText(MeetingActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Meeting> call, Throwable t) {
                    Toast.makeText(MeetingActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
    private void update(){
        getMeeting(pid);
        if (meeting!=null) {
            getSupportActionBar().setTitle(meeting.getName());
            ((TextView) findViewById(R.id.meetingTimeDate)).setText(String.valueOf(meeting.getDate()) + ":" + meeting.getTime());
            ((TextView) findViewById(R.id.meetingAddress)).setText(meeting.getAdress());
            users = meeting.getUsers();
            adapter.notifyDataSetChanged();
        }else{
            finish();
        }
    }

    private class MeetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            Toast.makeText(MeetingActivity.this,"update meeting",Toast.LENGTH_SHORT).show();
            getMeeting(pid);
        }

    }
    public void updateMeetingData (View view){
        update();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(meetReceiver);
    }
}
