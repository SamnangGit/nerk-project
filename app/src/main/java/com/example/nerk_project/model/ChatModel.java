package com.example.nerk_project.model;

import java.util.Date;


public class ChatModel {
    private String messageText;
    private String messageUser;
    private long messageTime;

    public ChatModel() {}

    public ChatModel(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageTime = new Date().getTime();
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }
}
