package com.example.campusapp.entity;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String name;
    private String password;
    private int type;
    private String realname;
    private String imageurl;
    private String email;
    private String sex;

    public User() {
    }

    public User(String username, String password) {
        this.name = username;
        this.password = password;
    }

    private boolean isUpdate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public int getType() {
        return type;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setType(int type) {
        this.type = type;
    }



    public boolean getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
