package com.example.fedditandroid;

public class Post {
    public String postTitle, postText, userPostID;
    public int likeCounter, commentCounter;

    public int getLikeCounter() {
        return likeCounter;
    }

    public void setLikeCounter(int likeCounter) {
        this.likeCounter = likeCounter;
    }

    public Post(String postTitle, String postText, String userPostID, int likeCounter) {
        this.postTitle = postTitle;
        this.postText = postText;
        this.userPostID = userPostID;
        this.likeCounter = likeCounter;

    }

    public Post() {
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getUserPostID() {
        return userPostID;
    }

    public void setUserPostID(String userPostID) {
        this.userPostID = userPostID;
    }

}
