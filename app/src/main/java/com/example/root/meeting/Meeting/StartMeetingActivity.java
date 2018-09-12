package com.example.root.meeting.Meeting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 08.04.18.
 */

public class StartMeetingActivity extends AppCompatActivity{
    private ArrayList<Meeting> meetings = new ArrayList<>();
    private ArrayAdapter<Meeting> adapter;
    Gson gson =new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_meeting);
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
    public void createMeeting(View view){
        startActivity(new Intent(this,CreateMeeting.class));
    }
    private void updateData(){
        App.getApi().getAllMeetings(MainActivity.getAuthToken()).enqueue(new Callback<List<Meeting>>() {
            @Override
            public void onResponse(Call<List<Meeting>> call, Response<List<Meeting>> response) {
                if (response.body() != null) {
                    if (response.code()==200) {
                            meetings.clear();
                            meetings.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }
                }else if (response.code()==401){
                    Toast.makeText(StartMeetingActivity.this,"bad auth values",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(StartMeetingActivity.this, AuthWind.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<Meeting>> call, Throwable t) {
                Toast.makeText(StartMeetingActivity.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void exit (View view){
        updateData();
    }
}
