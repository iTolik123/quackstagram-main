package com.example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CommentUi{

    CommentLogistic commentManager;
    String post;
    private Panel commentsDisplay;

    public CommentUi() {
        this.commentManager = new CommentLogistic();
    }

    public Panel showCommentsWithAuthor(String postId) {
        List<Comment> comments = commentManager.getCommentsUnderPost(postId);
        Panel commentPanel = new Panel();
        commentPanel.setLayout(new BorderLayout());
        TextArea commentArea = new TextArea(10, 40);
        commentArea.setEditable(false);

        for (Comment comment : comments) {
            String commentText = comment.getAuthor() + ": " + comment.getContent() + "\n\n";
            commentArea.append(commentText);
        }

        commentPanel.add(commentArea, BorderLayout.CENTER);
        return commentPanel;
    }

    public Panel openCommentWindow(String postId, String author) {
        Panel commentPanel = new Panel();
        commentPanel.setLayout(new BorderLayout());

        // Inizializza il pannello dei commenti
        commentsDisplay = showCommentsWithAuthor(postId);
        commentPanel.add(commentsDisplay, BorderLayout.CENTER);

        // Pannello di input per inserire nuovi commenti
        Panel inputPanel = new Panel(new FlowLayout());
        Label contentLabel = new Label("Comment:");
        TextField contentField = new TextField(30);
        Button submitButton = new Button("Submit Comment");

        inputPanel.add(contentLabel);
        inputPanel.add(contentField);
        inputPanel.add(submitButton);

        commentPanel.add(inputPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String content = contentField.getText();
                if (content.length() == 0 || content.length() > 250) {
                    
                } else {

                    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    if (!content.isEmpty()) {
                        Comment newComment = new Comment(content, postId, "", author, date);
                        commentManager.addComment(newComment);
                        commentManager.saveCommentsToFile();

                        String name = postId.split("\\\\")[postId.split("\\\\").length - 1];

                        //TODO insert notificationns?


                        contentField.setText(""); // Svuota il campo di input

                        // Aggiorna il pannello dei commenti senza crearne un altro
                        commentPanel.remove(commentsDisplay);
                        commentsDisplay = showCommentsWithAuthor(postId);
                        commentPanel.add(commentsDisplay, BorderLayout.CENTER);

                        commentPanel.revalidate();
                        commentPanel.repaint();
                    }
                }
            }
        });

        return commentPanel;
    }
}
