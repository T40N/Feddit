package com.example.fedditandroid;

public class Comment {
    String comment, author, authorID;

    public Comment() {
    }

    public Comment(String comment, String author, String authorID) {
        this.comment = comment;
        this.author = author;
        this.authorID = authorID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }
}
