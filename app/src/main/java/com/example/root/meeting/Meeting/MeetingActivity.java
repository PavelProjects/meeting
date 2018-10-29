package com.example.root.meeting.Meeting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.root.meeting.ObRealm.Meeting;
import com.example.root.meeting.ObRealm.Message;
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
    private List<Message> messages = new ArrayList<>();
    private ArrayAdapter<Message> adapter;
    private Meeting meeting =  new Meeting();
    private int pid = 0;
    public static String ACTION_UPDATE = "update";
    private MeetReceiver meetReceiver;
    Gson gson = new Gson();
    private Toolbar toolbar;

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
        adapter = new ArrayAdapter<Message>(this,
                android.R.layout.simple_list_item_1,android.R.id.text1, messages){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final Message message = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(message.getData().getMessage());
                return view;
            }
        };
        lvMain.setAdapter(adapter);
        update();
        update();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_button,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_update: update();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        meetReceiver = new MeetReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE);
        registerReceiver(meetReceiver,intentFilter);
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

    public void meetingSettings (View view){
        startActivityForResult(new Intent(MeetingActivity.this,MeetingSettings.class).putExtra("meeting",gson.toJson(meeting)),3);
    }

    private class MeetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getIntExtra("id",pid)==pid){
                Toast.makeText(MeetingActivity.this,"message!",Toast.LENGTH_SHORT).show();
                Toast.makeText(MeetingActivity.this,arg1.getStringExtra("message"),Toast.LENGTH_LONG).show();
            }
            Toast.makeText(MeetingActivity.this,"update meeting",Toast.LENGTH_SHORT).show();
            update();
        }

    }

    public void sendMessage(View view){
        String mes = ((EditText)findViewById(R.id.messageText)).getText().toString();
        if (mes.length()>0){
            Message message =new Message();
            Message.Data data =new Message.Data();
            data.setMeeting(meeting);
            data.setMessage(mes);
            data.setEvent("message");
            message.setData(data);
            message.setTo("meeting");
            Log.d("MessageLogs",gson.toJson(message));
            App.getApi().sendMessage(MainActivity.getAuthToken(),message).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(MeetingActivity.this,"sande",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }
    private void updateMessages(){
        App.getApi().getMessages(MainActivity.getAuthToken(),meeting.getId()).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful()){
                    messages.clear();
                    messages.addAll(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(MeetingActivity.this,t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(meetReceiver);
    }
}
