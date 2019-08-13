package com.example.hp.signin;

public class Message {

    String userName;
    String senderUserID;
    long timeStamp;
    String messageText;
    String state;

    public Message(){


    }

    public Message(String userName, String senderUserID, long timeStamp, String messageText, String state) {
        this.userName = userName;
        this.senderUserID = senderUserID;
        this.timeStamp = timeStamp;
        this.messageText = messageText;
        this.state = state;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSenderUserID() {
        return senderUserID;
    }

    public void setSenderUserID(String senderUserID) {
        this.senderUserID = senderUserID;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
