package com.example.root.meeting.ObRealm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 08.04.18.
 */

public class Meeting{
    private int id;
    private String name;
    private int admin;
    private List<User> users= new ArrayList<>();

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
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
        for (User ur:user){
            if (!users.equals(ur)){
                users.add(ur);
            }
        }
    }
}
