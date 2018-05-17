package com.example.root.meeting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.meeting.ObRealm.User;

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
        getFriends();
        ((TextView)findViewById(R.id.userNameProfile)).setText(user.getUsername());
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
    private void getUser(){
        App.getApi().authUser(MainActivity.getAuthToken()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    user = response.body();
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
                    user.addAllFriends(response.body());
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
        getFriends();
        adapter.notifyDataSetChanged();
    }
}
