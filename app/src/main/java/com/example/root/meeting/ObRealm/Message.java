package com.example.root.meeting.ObRealm;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by root on 23.07.18.
 */

public class Message implements Serializable{
    private Data data;
    private String to;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
    public Data getData(){
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{
        private int id;
        private String message;
        private String f;
        private String event;
        private User user;
        private Meeting meeting;
        private String info;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String inf) {
            this.info = inf;
        }

        public String getEvent() {
            return event;
        }

        public User getUser() {
            return user;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Meeting getMeeting() {
            return meeting;
        }

        public void setMeeting(Meeting meeting) {
            this.meeting = meeting;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getF() {
            return f;
        }

        public void setF(String f) {
            this.f = f;
        }
    }
}
