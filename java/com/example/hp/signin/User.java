package com.example.hp.signin;

import java.util.ArrayList;

public class User {

    String name;
    String email;
    String userID;
    String url;
    String accounttype;
    ArrayList<String> projectID;
    public User(){


    }

    public User(String name, String email, String userID, String url, ArrayList <String> projectID,String accounttype) {
        this.name = name;
        this.email = email;
        this.userID = userID;
        this.url = url;
        this.accounttype = accounttype;
        this.projectID = projectID;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAccounttype() {
        return accounttype;
    }

    public void setAccounttype(String accounttype) {
        this.accounttype = accounttype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList <String> getProjectID() {
        return projectID;
    }

    public void setProjectID(ArrayList <String> projectID) {
        this.projectID = projectID;
    }
}
