package com.example.root.meeting.ObRealm;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 01.04.18.
 */

public class User{
    private String id;
    private String username;
    private String role;
    private List<User> friends =new ArrayList<>();

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username=username;
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
}
