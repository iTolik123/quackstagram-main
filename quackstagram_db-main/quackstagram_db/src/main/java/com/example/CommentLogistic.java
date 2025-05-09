package com.example;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CommentLogistic {
    private List<Comment> comments;
    private static final String FILE_PATH = "data/comments.txt"; // Fixed path

    public CommentLogistic() {
        comments = new ArrayList<>();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<Comment> getCommentsUnderPost(String postId) {
        List<Comment> commentsUnderPost = new ArrayList<>();
        //TODO
        //implementation to get comments under a specific post from db

        return commentsUnderPost;
    }
    public void saveCommentsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            for (Comment comment : comments) {
                writer.println(comment.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
