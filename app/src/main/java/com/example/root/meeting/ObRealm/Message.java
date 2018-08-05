package com.example.root.meeting.ObRealm;

/**
 * Created by root on 23.07.18.
 */

public class Message {
    private int id;
    private String fu_id;
    private String su_id;
    private String body;
    private String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public String getFu_id() {
        return fu_id;
    }

    public String getSu_id() {
        return su_id;
    }

    public String getTitle() {
        return title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setFu_id(String fu_id) {
        this.fu_id = fu_id;
    }

    public void setSu_id(String su_id) {
        this.su_id = su_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
