package com.example.hp.signin;

import java.io.Serializable;
import java.util.HashMap;

public class Project implements Serializable {
    String projectID;
    String title;
    String description;
    int dayOfMonth;
    int month;
    int year;

    public Project(){

    }

    public Project(String projectID, String title, String description, int dayOfMonth, int month, int year) {
        this.projectID = projectID;
        this.title = title;
        this.description = description;
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.year = year;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
