package com.example;

public class Comment {
    private String content;
    private String postID;
    private String answer;
    private String author;
    private String date;

    public Comment() {
        this.content = "";
        this.postID = "";
        this.answer = "";
        this.author = "";
        this.date = "";
    }

    public Comment(String content, String postID, String answer, String author, String date) {
        this.content = content;
        this.postID = postID;
        this.answer = answer;
        this.author = author;
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public String getPostID() {
        return postID;
    }

    public String getAnswer() {
        return answer;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return content + ";" + postID + ";" + answer + ";" + author + ";" + date + ";\n";
    }
}
