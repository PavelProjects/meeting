package com.example.root.meeting.ObRealm;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by root on 01.04.18.
 */

public class User implements Serializable {
    private String id;
    private String name;
    private String password;
    private String role;
    private String mail;
    private RealmList<User> friends =new RealmList<>();

    public String getUsername(){
        return name;
    }
    public void setUsername(String name){
        this.name=name;
    }
    public String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserrole(String userrole) {
        this.role = userrole;
    }

    public String getUserrole() {
        return role;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public List<User> getFriends(){
        return friends;
    }
    public void addFriend(User user){
        friends.add(user);
    }
    public void removeFriend(User user){
        friends.remove(user);
    }
    public List<User> addAllFriends(List<User> friends){
        for (User user:friends){
            if (!this.friends.equals(user)){
                this.friends.add(user);
            }
        }
        return this.friends;
    }
    public void setPassword(String password){
        this.password=password;
    }
}
