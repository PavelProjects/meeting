package com.example.root.meeting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.root.meeting.ObRealm.Message;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging_activity);
    }
    public void send(View view){
        Message message = new Message();
        Message.Data data= new Message.Data();
        data.setMessage("hello");
        data.setFrom("pavel");
        message.setData(data);
        message.setTo("eoGvxLwDqzs:APA91bEIdLV6Y2cHB5vpC56dTQkqpFMyvvYVPUmvEHUQHbC7PFjSrpClAp44b6itL-Bfj5T7RovEomQnuIHyYq3H_fACaCcz40r5g-sR-jIBXl8gO-c9g85qqE-_vXnBNtiyYMUAUY6LLnXsicu38ysuKnHTmRqwSg");
        App.getApi().sendMessage(MainActivity.getAuthToken(),message).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("fLog",response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fLog",t.getLocalizedMessage());
            }
        });
    }
}
