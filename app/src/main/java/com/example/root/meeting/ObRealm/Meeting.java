package com.example.root.meeting.ObRealm;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 08.04.18.
 */

public class Meeting implements Serializable{
    private int id;
    private String name;
    private String admin;
    private ArrayList<User> users= new ArrayList<>();
    private Date date;
    private String time;
    private String adress;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }
    public void addUser (User user){
        users.add(user);
    }
    public void addAllUsers(List<User> user){
        users.clear();
        users.addAll(user);
    }

    public Date getDate() {
        return date;
    }

    public String getAdress() {
        return adress;
    }

    public String getTime() {
        return time;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
