package com.example.hp.signin;

import java.io.Serializable;

public class Task implements Serializable {
    String title;
    String description;
    int dayOfMonth;
    int month;
    int year;
    String taskID;

    public Task() {
    }

    public Task(String title, String description, int dayOfMonth, int month, int year, String taskID) {
        this.title = title;
        this.description = description;
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.year = year;
        this.taskID = taskID;
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

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }
}

