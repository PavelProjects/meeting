package com.example.root.meeting.Profile;

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

import com.example.root.meeting.MainActivity;
import com.example.root.meeting.ObRealm.User;
import com.example.root.meeting.R;
import com.example.root.meeting.apis.App;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class addFiend extends AppCompatActivity {
    private ArrayAdapter<User> adapter;
    private List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend);

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
                Intent intent = new Intent();
                add(users.get(position));
                intent.putExtra("flag","update");
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
    public void FindNewFriend(View view){
        App.getApi().getUserByName(MainActivity.getAuthToken(),((EditText)findViewById(R.id.findUserByName)).getText().toString()).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.body() != null) {
                    if (response.code()==200) {
                        users.clear();
                        users.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }else if (response.code()==401){
                        Toast.makeText(addFiend.this,"bad auth values",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(addFiend.this,"internet problems",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(addFiend.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void add(User user){
        App.getApi().addFriend(MainActivity.getAuthToken(),user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.code()==200) {
                        Toast.makeText(addFiend.this,"added",Toast.LENGTH_SHORT).show();
                    }else if (response.code()==401){
                        Toast.makeText(addFiend.this,"bad auth values",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(addFiend.this,"internet problems",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(addFiend.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
