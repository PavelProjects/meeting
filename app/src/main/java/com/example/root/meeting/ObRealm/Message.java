package com.example.root.meeting.ObRealm;

import java.io.Serializable;

/**
 * Created by root on 23.07.18.
 */

public class Message implements Serializable{
    private MessagingData data;
    private String to;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
    public MessagingData getData(){
        return data;
    }

    public void setData(MessagingData data) {
        this.data = data;
    }

}
