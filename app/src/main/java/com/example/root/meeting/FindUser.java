package com.example.root.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.meeting.ObRealm.User;
import com.example.root.meeting.apis.App;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 28.04.18.
 */

public class FindUser extends AppCompatActivity {
    private ArrayAdapter<User> adapter;
    private List<User> users = new ArrayList<>();
    Gson gson =new Gson();
    int pid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user_activity);
        pid = getIntent().getIntExtra("id",1);
        ListView lvMain = (ListView) findViewById(R.id.userslist);
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
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                setResult(RESULT_OK,new Intent().putExtra("user",gson.toJson(users.get(position))));
                finish();
            }
        });
    }

    public void find(View view){
        App.getApi().getUserByName(MainActivity.getAuthToken(),((EditText)findViewById(R.id.userName)).getText().toString()).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.body() != null) {
                    if (response.code()==200) {
                        users.clear();
                        users.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }else if (response.code()==401){
                        Toast.makeText(FindUser.this,"bad auth values",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(FindUser.this,"internet problems",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(FindUser.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
