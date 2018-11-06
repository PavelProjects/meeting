package com.example.root.meeting.Meeting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.meeting.MainActivity;
import com.example.root.meeting.ObRealm.MessagingData;
import com.example.root.meeting.ObRealm.Meeting;
import com.example.root.meeting.ObRealm.Message;
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
    private List<MessagingData> messages = new ArrayList<>();
    private ArrayAdapter<MessagingData> adapter;
    private Meeting meeting =  new Meeting();
    private int pid = 0;
    public static String ACTION_UPDATE = "update";
    public static String ACTION_NEW_MESSAGE = "NEW_MESSAGE";
    private Gson gson = new Gson();
    private MeetReceiver meetReceiver = new MeetReceiver();
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_activity);
        toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        meeting = gson.fromJson(intent.getStringExtra("meeting"),Meeting.class);
        if (meeting==null){
            finish();
        }
        pid = meeting.getId();
        if (pid==0){
            finish();
        }
        ((TextView) findViewById(R.id.meetingTimeDate)).setText(String.valueOf(meeting.getDate()) + ":" + meeting.getTime());
        ((TextView) findViewById(R.id.meetingAddress)).setText(meeting.getAdress());
        updateMessages();
        ListView lvMain = (ListView) findViewById(R.id.messages);
        adapter = new ArrayAdapter<MessagingData>(this,
                android.R.layout.two_line_list_item,android.R.id.text1, messages){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final MessagingData data = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(data.getMessage());
                ((TextView) view.findViewById(android.R.id.text2)).setText(data.getF());
                return view;
            }
        };
        lvMain.setAdapter(adapter);
        update();
        sharedPreferences=getSharedPreferences("UserKeys",MODE_PRIVATE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_button,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                update();
                return true;
            case R.id.action_settings:
                startActivityForResult(new Intent(MeetingActivity.this, MeetingSettings.class).putExtra("meeting", gson.toJson(meeting)), 3);
            default:
                return super.onOptionsItemSelected(item);
        }
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
                            meeting.addAllUsers(response.body().getUsers());
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
            if(meeting.getUsers()!=null) {
                getSupportActionBar().setTitle(meeting.getName());
                ((TextView) findViewById(R.id.meetingTimeDate)).setText(String.valueOf(meeting.getDate()) + ":" + meeting.getTime());
                ((TextView) findViewById(R.id.meetingAddress)).setText(meeting.getAdress());
                updateMessages();
                adapter.notifyDataSetChanged();
            }else{
                update();
            }
        }else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK){
            update();
        }
    }


    public void sendMessage(View view){
        String mes = ((EditText)findViewById(R.id.messageText)).getText().toString();
        if (mes.length()>0){
            final Message message =new Message();
            final MessagingData data =new MessagingData();
            data.setF(sharedPreferences.getString("username",""));
            data.setMessage(mes);
            data.setMid(meeting.getId());
            data.setEvent("message");
            message.setData(data);
            message.setTo("meeting");
            Log.d("MessageLogs",gson.toJson(message));
            App.getApi().sendMessage(MainActivity.getAuthToken(),message).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(MeetingActivity.this,"sande",Toast.LENGTH_SHORT).show();
                        ((EditText)findViewById(R.id.messageText)).setText("");
                    }else {
                        Toast.makeText(MeetingActivity.this, "something gone wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }
    private void updateMessages(){
        App.getApi().getMessages(MainActivity.getAuthToken(),meeting.getId()).enqueue(new Callback<List<MessagingData>>() {
            @Override
            public void onResponse(Call<List<MessagingData>> call, Response<List<MessagingData>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        messages.clear();
                        messages.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MessagingData>> call, Throwable t) {
                Toast.makeText(MeetingActivity.this,t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(meetReceiver,new IntentFilter(ACTION_NEW_MESSAGE));
        update();
        activityResumed();
    }
    private class MeetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getIntExtra("id", 0) == pid) {
                MessagingData data = gson.fromJson(intent.getStringExtra("message"), MessagingData.class);
                messages.add(messages.size(),data);
                adapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(meetReceiver);
        super.onPause();
        activityPaused();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private static boolean activityVisible;
    public static boolean isActivityVisible(){
        return activityVisible;
    }
    private static void activityResumed() {
        activityVisible = true;
    }

    private static void activityPaused() {
        activityVisible = false;
    }

}
