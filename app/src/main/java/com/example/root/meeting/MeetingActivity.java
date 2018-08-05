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

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingActivity extends AppCompatActivity {
    private List<User> users  = new ArrayList<>();
    private ArrayAdapter<User> adapter;
    Meeting meeting = new Meeting();
    private int pid = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_activity);
        Intent intent = getIntent();
        pid = intent.getIntExtra("id",pid);
        if (pid==0){
            finish();
        }
        getMeeting(pid);
        if (meeting!=null) {
            users = meeting.getUsers();
        }else{
            Toast.makeText(this,"cant find this meeting", Toast.LENGTH_SHORT).show();
            finish();
        }
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

    public void addUser(View view){
        startActivity(new Intent(MeetingActivity.this,AddUser.class).putExtra("id",pid));
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
    public void getMeeting(final int id){
        App.getApi().getMeeting(MainActivity.getAuthToken(),id).enqueue(new Callback<Meeting>() {
            @Override
            public void onResponse(Call<Meeting> call, Response<Meeting> response) {
                if (response.isSuccessful()){
                    if (response.body()!= null) {
                        meeting.setId(response.body().getId());
                        meeting.setName(response.body().getName());
                        getSupportActionBar().setTitle(response.body().getName());
                        meeting.setAdmin(response.body().getAdmin());
                        meeting.addAllUsers(response.body().getUsers());
                    }else{
                        getMeeting(id);
                    }
                }else{
                    Toast.makeText(MeetingActivity.this,"error",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Meeting> call, Throwable t) {
                Toast.makeText(MeetingActivity.this,t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}
