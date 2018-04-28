package com.example.root.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.meeting.ObRealm.Meeting;
import com.example.root.meeting.ObRealm.User;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 18.04.18.
 */

public class MeetingActivity extends AppCompatActivity {
    private RealmList<User> users  = new RealmList<>();
    private ArrayAdapter<User> adapter;
    Meeting meeting;
    private int pid = 0;
    Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_activity);
        pid = getIntent().getIntExtra("id",pid);
        if (pid==0){
            finish();
        }
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
        meeting = realm.where(Meeting.class).equalTo("id",pid).findFirst();
        if (meeting == null){
            Toast.makeText(this,"cant find this meeting", Toast.LENGTH_SHORT).show();
            finish();
        }
        ((TextView)findViewById(R.id.textView)).setText(meeting.getName());
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
    }
    private void updateData(){
        App.getApi().getMeeting(MainActivity.getAuthToken()).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, Response<Response> response) {
                if (response.body() != null) {
                    if (response.code()==200) {
                        realm.beginTransaction();
                        realm.createOrUpdateAllFromJson(Meeting.class,String.valueOf(response.body()));
                        realm.commitTransaction();
                    }
                }else if (response.code()==401){
                    Toast.makeText(MeetingActivity.this,"bad auth values",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MeetingActivity.this, AuthWind.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(MeetingActivity.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
