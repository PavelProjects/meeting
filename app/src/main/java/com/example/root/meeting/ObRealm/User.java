package com.example.root.meeting.ObRealm;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 01.04.18.
 */

public class User extends RealmObject {
    @PrimaryKey
    private int id;
    private String username;
    private String role;

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserrole(String userrole) {
        this.role = userrole;
    }

    public String getUserrole() {
        return role;
    }
}
