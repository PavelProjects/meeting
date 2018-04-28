package com.example.root.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.meeting.ObRealm.Meeting;
import com.example.root.meeting.ObRealm.User;

import org.json.JSONArray;

import io.realm.Realm;
import io.realm.RealmList;
import okhttp3.ResponseBody;
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
        Intent intent = getIntent();
        pid = intent.getIntExtra("id",pid);
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
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user =users.get(i);
                deleteUser(user.getId());
                realm.beginTransaction();
                user.deleteFromRealm();
                realm.commitTransaction();
                adapter.notifyDataSetChanged();
            }
        });
        if (intent.getStringExtra("flag")!=null) {
            if (intent.getStringExtra("flag").equals("update")) {
                updateData();
            }
        }
    }

    public void addUser(View view){
        startActivity(new Intent(MeetingActivity.this,AddUser.class).putExtra("id",pid));
        finish();
    }
    private void deleteUser(int uid){
        App.getApi().deleteUserFromMeeting(MainActivity.getAuthToken(),pid,uid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    public void updateData(){
        App.getApi().getMeeting(MainActivity.getAuthToken(),pid).enqueue(new Callback<Meeting>() {
            @Override
            public void onResponse(Call<Meeting> call, Response<Meeting> response) {
                if (response.body() != null) {
                    if (response.code()==200) {
                        realm.beginTransaction();
                        meeting = realm.copyToRealmOrUpdate(response.body());
                        realm.commitTransaction();
                    }
                }else if (response.code()==401){
                    Toast.makeText(MeetingActivity.this,"bad auth values",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MeetingActivity.this, AuthWind.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Meeting> call, Throwable t) {
                Toast.makeText(MeetingActivity.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        ((TextView)findViewById(R.id.textView)).setText(meeting.getName());
        users = meeting.getUsers();
        adapter.notifyDataSetChanged();
    }

}
