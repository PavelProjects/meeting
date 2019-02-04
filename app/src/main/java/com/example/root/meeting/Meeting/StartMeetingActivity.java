package com.example.root.meeting.Meeting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.meeting.Auth_Reg.AuthWind;
import com.example.root.meeting.MainActivity;
import com.example.root.meeting.ObRealm.Meeting;
import com.example.root.meeting.R;
import com.example.root.meeting.apis.App;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 08.04.18.
 */

public class StartMeetingActivity extends AppCompatActivity{
    private ArrayList<Meeting> meetings = new ArrayList<>();
    private ArrayAdapter<Meeting> adapter;
    public static String ACTION_UPDATE = "update";
    private MeetReceiver meetReceiver = new MeetReceiver();
    Gson gson =new Gson();
    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_meeting);
        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        ListView lvMain = (ListView) findViewById(R.id.meeting);
        adapter = new ArrayAdapter<Meeting>(this,
                android.R.layout.simple_list_item_2,android.R.id.text1, meetings){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final Meeting meeting = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(meeting.getName());
                ((TextView) view.findViewById(android.R.id.text2)).setText(String.valueOf(meeting.getAdmin()));
                return view;
            }
        };
        lvMain.setAdapter(adapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(StartMeetingActivity.this,MeetingActivity.class);
                intent.putExtra("meeting", gson.toJson(meetings.get(position)));
                startActivity(intent);
            }
        });
        updateData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_button,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_update: updateData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(meetReceiver, new IntentFilter(ACTION_UPDATE));
    }

    public void createMeeting(View view){
        startActivity(new Intent(this,CreateMeeting.class));
    }
    private void updateData(){
        App.getApi().getAllMeetings(MainActivity.getAuthToken()).enqueue(new Callback<List<Meeting>>() {
            @Override
            public void onResponse(Call<List<Meeting>> call, Response<List<Meeting>> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        meetings.clear();
                        meetings.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }
                }else if (response.code()==401){
                    Toast.makeText(StartMeetingActivity.this,"bad auth values",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(StartMeetingActivity.this, AuthWind.class));
                    finish();
                }else{
                    Toast.makeText(StartMeetingActivity.this,"something gone wrong",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Meeting>> call, Throwable t) {
                Toast.makeText(StartMeetingActivity.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private class MeetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            updateData();
        }

    }
    public void exit (View view){
        updateData();
    }
}
