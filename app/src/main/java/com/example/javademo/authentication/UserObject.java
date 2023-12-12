package com.example.javademo.authentication;

public class UserObject {
    private String fullName = "";

    public UserObject create(String fullName){
        this.fullName = fullName;
        return this;
    }


    public String getFullName(){
        return this.fullName;
    }


}
