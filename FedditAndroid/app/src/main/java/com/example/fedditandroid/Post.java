package com.example.fedditandroid;

public class Post {
    public String postTitle, postText, userPostID;

    public Post() {
    }

    public Post(String postTitle, String postText, String userPostID) {
        this.postTitle = postTitle;
        this.postText = postText;
        this.userPostID = userPostID;
    }
}
