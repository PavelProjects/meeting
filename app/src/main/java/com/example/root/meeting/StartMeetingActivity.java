package com.example.root.meeting;

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

import com.example.root.meeting.ObRealm.Meeting;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 08.04.18.
 */

public class StartMeetingActivity extends AppCompatActivity{
    private RealmList<Meeting> meetings = new RealmList<>();
    private ArrayAdapter<Meeting> adapter;
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_meeting);
        Realm.init(getApplicationContext());
        realm=Realm.getDefaultInstance();
        meetings.addAll(realm.where(Meeting.class).findAll());
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
                intent.putExtra("id",meetings.get(position).getId());
                startActivity(intent);
            }
        });
        updateData();
    }
    private void updateData(){
        App.getApi().getMeetings(MainActivity.getAuthToken()).enqueue(new Callback<List<Meeting>>() {
            @Override
            public void onResponse(Call<List<Meeting>> call, Response<List<Meeting>> response) {
                if (response.body() != null) {
                    if (response.code()==200) {
                        meetings.clear();
                        realm.beginTransaction();
                        meetings.addAll(response.body());
                        realm.copyToRealmOrUpdate(meetings);
                        realm.commitTransaction();
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
