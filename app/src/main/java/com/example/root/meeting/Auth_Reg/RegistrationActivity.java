package com.example.root.meeting.Auth_Reg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.root.meeting.ObRealm.User;
import com.example.root.meeting.R;
import com.example.root.meeting.apis.App;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.UnsupportedEncodingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    int code = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
    }
    public void signIn (View view){

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        String name = ((EditText)findViewById(R.id.registrationUserName)).getText().toString();
        String password = ((EditText)findViewById(R.id.registrationUserPassword)).getText().toString();
        String mail = ((EditText)findViewById(R.id.userMail)).getText().toString();

        if (name.length() != 0 && password.length() != 0 && mail.length() != 0) {
            User user = new User();
            user.setId(refreshedToken);
            user.setUsername(name);
            user.setPassword(password);
            user.setMail(mail);
            user.setUserrole("user");

            byte[] data = new byte[0];
            String np = "auth:auth";
            try {
                data = np.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String auth = "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);

            App.getApi().createUser(auth, user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this, "created " + response.body().getUsername(), Toast.LENGTH_SHORT).show();
                        RegistrationActivity.this.finish();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(RegistrationActivity.this, "fail" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this,"Не все поля заполнены",Toast.LENGTH_SHORT).show();
        }
    }
}
