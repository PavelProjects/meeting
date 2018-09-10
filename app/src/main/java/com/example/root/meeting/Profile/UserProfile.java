package com.example.root.meeting.Profile;

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

import com.example.root.meeting.MainActivity;
import com.example.root.meeting.ObRealm.User;
import com.example.root.meeting.R;
import com.example.root.meeting.apis.App;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 29.04.18.
 */

public class UserProfile extends AppCompatActivity {
    private ArrayAdapter<User> adapter;
    private List<User> friends = new ArrayList<>();
    private User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_acivity);
        getUser();
        ListView listView = (ListView) findViewById(R.id.friends_list);
        adapter= new ArrayAdapter<User>(this,android.R.layout.simple_list_item_1,friends){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final User user = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(user.getUsername());
                return view;
            }
        };
        listView.setAdapter(adapter);
    }
    public void addFriends(View view){
        startActivityForResult(new Intent(this,addFiend.class),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            if (data.getBooleanExtra("flag",true)){
                getFriends();
            }
        }
    }

    private void getUser(){
        App.getApi().authUser(MainActivity.getAuthToken()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    if (response.body()!=null) {
                        user = response.body();
                        ((TextView) findViewById(R.id.userNameProfile)).setText(user.getUsername());
                        getFriends();
                    }else{
                        getUser();
                    }
                }else{
                    Toast.makeText(UserProfile.this,"error",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserProfile.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getFriends(){
        App.getApi().getFriends(MainActivity.getAuthToken()).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    if (response.body()!=null) {
                        friends.clear();
                        friends.addAll(user.addAllFriends(response.body()));
                        adapter.notifyDataSetChanged();
                    }
                }else{
                    Toast.makeText(UserProfile.this,"error",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(UserProfile.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void update(View view){
        getUser();
        adapter.notifyDataSetChanged();
    }
}
