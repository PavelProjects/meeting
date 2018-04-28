package com.example.root.meeting.ObRealm;

import javax.annotation.Nonnull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 08.04.18.
 */

public class Meeting extends RealmObject {
    @PrimaryKey
    private int id;
    @Nonnull
    private String name;
    private int admin;
    private RealmList<User> users= new RealmList<>();

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

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    public RealmList<User> getUsers() {
        return users;
    }
    public void addUser (User user){
        users.add(user);
    }
}
