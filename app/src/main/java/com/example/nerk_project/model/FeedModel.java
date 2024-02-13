package com.example.nerk_project.model;

import com.google.firebase.Timestamp;

public class FeedModel {
    private String userInput;
    private String imagePath;
    private String userId;
    private Timestamp time;

    public FeedModel() {
    }

    public FeedModel(String userInput, String imagePath, String userId, Timestamp time) {
        this.userInput = userInput;
        this.imagePath = imagePath;
        this.userId = userId;
        this.time = time;
    }

    public String getUserInput() {
        return userInput;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getUserId() {
        return userId;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



    public void setTime(Timestamp time) {
        this.time = time;
    }
}