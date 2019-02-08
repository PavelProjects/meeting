package com.example.root.meeting.ArrayAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.root.meeting.Meeting.MeetingActivity;
import com.example.root.meeting.ObRealm.MessagingData;
import com.example.root.meeting.R;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<MessagingData> {
    List<MessagingData> messages;
    Context context;

    public MessageAdapter(@NonNull Context context, int resource, @NonNull List<MessagingData> objects) {
        super(context, resource, objects);
        this.messages = objects;
        this.context = context;
    }


    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public MessagingData getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        MessagingData message = messages.get(i);

        if (message.getF().equals(MeetingActivity.userMail)){
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getMessage());
        }else{
            convertView = messageInflater.inflate(R.layout.their_messages, null);
            holder.avatar = (View) convertView.findViewById(R.id.avatar);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);

            holder.name.setText(message.getF());
            holder.messageBody.setText(message.getMessage());
        }

        return convertView;
    }

    class MessageViewHolder {
        public View avatar;
        public TextView name;
        public TextView messageBody;
    }
}
