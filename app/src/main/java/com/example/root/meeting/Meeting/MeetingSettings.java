package com.example.root.meeting.Meeting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.meeting.FindUser;
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

public class MeetingSettings extends AppCompatActivity {
    private List<User> users =new ArrayList<>();
    private ArrayAdapter<User> adapter;
    private Gson gson = new Gson();
    private Meeting meeting;
    private int pid;
    private boolean flag;
    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_settings);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);

        Intent intent = getIntent();
        meeting = gson.fromJson(intent.getStringExtra("meeting"),Meeting.class);
        if (meeting==null){
            finish();
        }
        pid = meeting.getId();
        ((EditText)findViewById(R.id.newName)).setText(meeting.getName());
        users.addAll(meeting.getUsers());
        ListView lvMain = (ListView) findViewById(R.id.participants);
        adapter = new ArrayAdapter<User>(this,android.R.layout.simple_list_item_1,android.R.id.text1, users){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final User user = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(user.getUsername());
                return view;
            }
        };

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                deleteUser(users.get(position));
            }
        });
        lvMain.setAdapter(adapter);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2: if (resultCode == RESULT_OK) {
                    User user = gson.fromJson(data.getStringExtra("user"),User.class);
                    addUserToParty(pid,user);
                }
                break;
            case 3:
                meeting.setLatitude(data.getDoubleExtra("latitude",0));
                meeting.setLongitude(data.getDoubleExtra("longitude",0));
                savePlace(meeting);
        }
    }


    private void update(){
        App.getApi().getMeeting(MainActivity.getAuthToken(), pid).enqueue(new Callback<Meeting>() {
            @Override
            public void onResponse(Call<Meeting> call, Response<Meeting> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        meeting.setName(response.body().getName());
                        meeting.setDate(response.body().getDate());
                        meeting.setTime(response.body().getTime());
                        meeting.addAllUsers(response.body().getUsers());
                    }
                } else {
                    Toast.makeText(MeetingSettings.this, "error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Meeting> call, Throwable t) {
                Toast.makeText(MeetingSettings.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        ((EditText)findViewById(R.id.newName)).setText(meeting.getName());
        users = meeting.getUsers();
        adapter.notifyDataSetChanged();
    }
    private void addUserToParty(int pid, final User user){
        App.getApi().addUserToMeeting(MainActivity.getAuthToken(),pid,user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        users.add(user);
                        adapter.notifyDataSetChanged();
                    }else if (response.code()==401){
                        Toast.makeText(MeetingSettings.this,"bad auth values",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MeetingSettings.this,"internet problems",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MeetingSettings.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void changeNameSet (View view){
        String name  = ((EditText)findViewById(R.id.newName)).getText().toString();
        if(name != null){
            meeting.setId(pid);
            meeting.setName(name);
            App.getApi().updateMeetingName(MainActivity.getAuthToken(), meeting).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(MeetingSettings.this,"ok",Toast.LENGTH_SHORT).show();
                        update();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(MeetingSettings.this,t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void changePlace(View view){
        startActivityForResult(new Intent(MeetingSettings.this,ChangePlace.class),3);
    }
    private void savePlace(Meeting meeting){
        App.getApi().updateMeetingPlace(MainActivity.getAuthToken(),meeting).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Toast.makeText(MeetingSettings.this,"changed",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MeetingSettings.this,t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void addUser(View view){
        startActivityForResult(new Intent(MeetingSettings.this, FindUser.class).putExtra("id",pid),2);
    }
    private boolean deleteUser(final User user){
        flag= false;
        App.getApi().deleteUserFromMeeting(MainActivity.getAuthToken(),pid,user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    users.remove(user);
                    adapter.notifyDataSetChanged();
                    flag=true;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
        return flag;
    }
    public void saveChanges (View view){
        setResult(RESULT_OK);
        finish();
    }
    public void returnBack (View view){
        setResult(RESULT_CANCELED);
        finish();
    }

}
