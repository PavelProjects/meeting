package com.example.root.meeting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.meeting.Auth_Reg.AuthWind;
import com.example.root.meeting.Meeting.MeetingActivity;
import com.example.root.meeting.Meeting.MeetingPointMap;
import com.example.root.meeting.Meeting.StartMeetingActivity;
import com.example.root.meeting.ObRealm.User;
import com.example.root.meeting.Profile.UserProfile;
import com.example.root.meeting.apis.App;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences userKey;
    private static String userNp;
    private SharedPreferences.Editor editor;
    private List<User> users = new ArrayList<User>();
    boolean flag = false;
    private ArrayAdapter<User> adapter;
    private static String token = FirebaseInstanceId.getInstance().getToken();
    private Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        startService(new Intent(MainActivity.this, FirebaseMessagingService.class));
        userKey=getSharedPreferences("UserKeys",MODE_PRIVATE);
        userNp=userKey.getString("username","")+":"+userKey.getString("password","");

        checkAuth();

        ListView lvMain = (ListView) findViewById(R.id.userslist);
        adapter = new ArrayAdapter<User>(this,
                android.R.layout.two_line_list_item,android.R.id.text1,users){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final User user = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(user.getUsername());
                ((TextView) view.findViewById(android.R.id.text2)).setText(user.getUserrole());
                return view;
            }
        };
        MeetingActivity.userMail=userKey.getString("mail","");
        lvMain.setAdapter(adapter);
        updateData();
        checkToken();
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

    private void checkToken(){
        if (token!=null) {
            Log.d("fLog",token);
            App.getApi().authUser(MainActivity.getAuthToken()).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getId() != null) {
                            if (response.body().getId().equals(token)) {
                                flag = true;
                            } else {
                                flag = false;
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            if (!flag) {
                App.getApi().updateToken(getAuthToken(), token).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                editor = userKey.edit();
                editor.putString("id", token);
                editor.apply();
                Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @NonNull
    public static String getAuthToken() {
        byte[] data = new byte[0];
        try {
            data = (MainActivity.userNp).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
    }
    public void updateData(){
        App.getApi().getFriends(getAuthToken()).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        users.clear();
                        users.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }else if (response.code()==401){
                        Toast.makeText(MainActivity.this,"bad auth values",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAuth(){
        if (userNp!=null){
            if (userNp.length()<=2) {
                startActivity(new Intent(MainActivity.this, AuthWind.class));
            }
        }
    }
    public void startM(View view){
        startActivity(new Intent(MainActivity.this,StartMeetingActivity.class));
    }
    public void userProfile(View view){
        startActivity(new Intent(MainActivity.this,UserProfile.class).putExtra("id",userKey.getString("id","")));
    }
    public void userExit(View view) throws IOException {
        editor=userKey.edit();
        editor.putString("username",null);
        editor.putString("password",null);
        editor.apply();
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainActivity.this,FirebaseMessagingService.class));
    }

}
