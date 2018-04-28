package com.example.root.meeting;

/**
 * Created by root on 11.02.18.
 */import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("username")
    @Expose
    private String username;

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id=id;
    }
    public String getName() {
        return username;
    }
    public void setName(String site) {
        this.username = site;
    }

}
