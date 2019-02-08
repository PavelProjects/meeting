package com.example.root.meeting.Auth_Reg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.root.meeting.MainActivity;
import com.example.root.meeting.Meeting.MeetingActivity;
import com.example.root.meeting.ObRealm.Meeting;
import com.example.root.meeting.ObRealm.User;
import com.example.root.meeting.R;
import com.example.root.meeting.apis.App;

import java.io.UnsupportedEncodingException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 21.02.18.
 */

public class AuthWind extends AppCompatActivity {
    String username, password;
    EditText un, ps;
    SharedPreferences userKeys;
    SharedPreferences.Editor editor;
    private boolean code = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_activity);
        userKeys=getSharedPreferences("UserKeys",MODE_PRIVATE);
        un = (EditText) findViewById(R.id.username);
        ps = (EditText) findViewById(R.id.password);
        if (userKeys!=null) {
                un.setText(userKeys.getString("username",""));
                ps.setText(userKeys.getString("password",""));
        }
    }

    public void authUser(View view) {
        username = un.getText().toString();
        password = ps.getText().toString();
        if (username != null && password != null) {
            check(username, password);
            if (code) {
                editor = userKeys.edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.apply();
                Toast.makeText(AuthWind.this,"Success",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AuthWind.this, MainActivity.class));
                this.finish();
            }else {
                Toast.makeText(AuthWind.this,"wrong auth values", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AuthWind.this, "need name&password", Toast.LENGTH_LONG).show();
        }

    }
    public static String getAuthToken(String name, String password) {
        byte[] data = new byte[0];
        String t= name +":"+password;
        try {
            data = (t).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
    }

    private void check(String username, String password){
        App.getApi().authUser(getAuthToken(username,password)).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    code = true;
                    editor = userKeys.edit();
                    editor.putString("id", response.body().getId());
                    editor.putString("mail", response.body().getMail());
                    editor.apply();
                    MeetingActivity.userMail = response.body().getMail();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(AuthWind.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void createAccount (View view){
        startActivity(new Intent(AuthWind.this,RegistrationActivity.class));
    }
}
