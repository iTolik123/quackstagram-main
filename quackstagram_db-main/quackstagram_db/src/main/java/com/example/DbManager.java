package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DbManager {

    private final String dbUrl = "jdbc:mysql://localhost:3306/quackstagram";
    private final String dbUsername = "root";
    private final String password = "";

    public DbManager() {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, password)) {
            String sql = readSqlFile("quackstagram_db-main/quackstagram_db/src/main/java/com/Schema.sql");
            System.out.println("Working Directory: " + new File(".").getAbsolutePath());
            for (String statement : sql.split(";")) {
                if (!statement.trim().isEmpty()) {
                    try (Statement stmnt = connection.createStatement()) {
                        stmnt.execute(statement);
                    }
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private String readSqlFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public User verifyCredentials(String username, String password) {
        String query = "SELECT * FROM User WHERE User.name = ? AND User.password = ?";
        String check_username = "";
        String check_password = "";
        ResultSet rs = null;

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password)) {
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setString(1, username);
            stmnt.setString(2, password);
            rs = stmnt.executeQuery();
            while (rs.next()) {
                check_username = rs.getString(2);
                check_password = rs.getString(3);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (check_password.equals(password) && check_username.equals(username)) {
            String bio = "";
            User newuser = new User(username, bio, password);
            saveUserInformation(newuser);
            return newuser;
        }
        return null;
    }

    public boolean verifyCredentials(String username) {
        String query = "SELECT * FROM User WHERE User.name = ?";
        String check_username = "";

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password)) {
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setString(1, username);
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) {
                check_username = rs.getString(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return check_username.equals(username);
    }

    private void saveUserInformation(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("quackstagram_db-main/quackstagram_db/src/data/users.txt", false))) {
            writer.write(user.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean insertUser(String username, String password, String bio) {
        String query = "INSERT INTO User(name, password, bio) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password)) {
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setString(1, username);
            stmnt.setString(2, password);
            stmnt.setString(3, bio);
            stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean insertUser(String username, String password, String bio, String imagePath) {
        String query = "INSERT INTO User(name, password, bio, profile_image) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password);
            FileInputStream fis = new FileInputStream(imagePath);) {
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setString(1, username);
            stmnt.setString(2, password);
            stmnt.setString(3, bio);
            
            stmnt.setBinaryStream(4, fis, new File(imagePath).length());

            stmnt.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    
    //TODO
    public void insertComment(String post_id, String comment) {
        //get the current user
        //get the current post
        //post the comment
        String username = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("quackstagram_db-main/quackstagram_db/src/data/users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split("::");
                username = userData[0];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String query = "INSERT INTO Comment(username, post_id, comment) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password)) {
            PreparedStatement stmnt = connection.prepareStatement(query);
            stmnt.setString(1, username);
            stmnt.setString(2, post_id);
            stmnt.setString(3, comment);
            stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public void insertFollower(String follower, String followed) {
        //get id of the follower
        //get id of the followed
        
        String followerId = null;
        String followedId = null;

        String getIdQuery = "SELECT id FROM User WHERE name = ?";
        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password)) {
            // Get follower ID
            try (PreparedStatement stmnt = connection.prepareStatement(getIdQuery)) {
                stmnt.setString(1, follower);
                try (ResultSet rs = stmnt.executeQuery()) {
                    if (rs.next()) {
                        followerId = rs.getString("id");
                    }
                }
            }

            // Get followed ID
            try (PreparedStatement stmnt = connection.prepareStatement(getIdQuery)) {
                stmnt.setString(1, followed);
                try (ResultSet rs = stmnt.executeQuery()) {
                    if (rs.next()) {
                        followedId = rs.getString("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (followerId == null || followedId == null) {
            throw new IllegalArgumentException("Follower or followed user does not exist.");
        }

        String checkQuery = "SELECT COUNT(*) AS count FROM Follower WHERE follower_id = ? AND followed_id = ?";
        boolean exists = false;

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password)) {
            //check if the relationship already exists
            try (PreparedStatement checkStmnt = connection.prepareStatement(checkQuery)) {
            checkStmnt.setString(1, followerId);
            checkStmnt.setString(2, followedId);
            try (ResultSet rs = checkStmnt.executeQuery()) {
                if (rs.next()) {
                exists = rs.getInt("count") > 0;
                }
            }
            }

            //insert only if it doesn't exist
            if (!exists) {
            String insertQuery = "INSERT INTO UserFollows(follower_id, followed_id) VALUES (?, ?)";
            try (PreparedStatement insertStmnt = connection.prepareStatement(insertQuery)) {
                insertStmnt.setString(1, followerId);
                insertStmnt.setString(2, followedId);
                insertStmnt.executeUpdate();
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public int getUserId(String username) {
        String query = "SELECT id FROM User WHERE name = ?";
        int userId = -1;

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password);
             PreparedStatement stmnt = connection.prepareStatement(query)) {

            stmnt.setString(1, username);
            try (ResultSet rs = stmnt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }
    public int getNumberOfFollowers(String username) {
        String query = "SELECT COUNT(*) AS follower_count FROM userfollows " +
                       "INNER JOIN User ON userfollows.followed_id = User.id " +
                       "WHERE User.name = ?";
        int followerCount = 0;

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password);
             PreparedStatement stmnt = connection.prepareStatement(query)) {

            stmnt.setString(1, username);
            try (ResultSet rs = stmnt.executeQuery()) {
                if (rs.next()) {
                    followerCount = rs.getInt("follower_count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return followerCount;
    }
    
    public void saveImage(String imagePath, String imageName, int userId, String bio) {
        String query = "INSERT INTO Post (postName, user_id, bio, image) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, password);
             PreparedStatement stmnt = conn.prepareStatement(query);
             FileInputStream fis = new FileInputStream(imagePath)) {

            stmnt.setString(1, imageName);
            stmnt.setInt(2, userId);
            stmnt.setString(3, bio);
            stmnt.setBinaryStream(4, fis, new File(imagePath).length());
            stmnt.executeUpdate();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    
    public void loadImage(int imageId, String destinationPath) {
        String query = "SELECT data FROM Images WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, password);
            PreparedStatement stmnt = conn.prepareStatement(query)) {

            stmnt.setInt(1, imageId);
            ResultSet rs = stmnt.executeQuery();

            if (rs.next()) {
                InputStream input = rs.getBinaryStream("data");
                Files.copy(input, Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    public java.util.List<ImageData> loadImagesFromDb() {
        java.util.List<ImageData> imageDataList = new java.util.ArrayList<>();
        String query = "SELECT postName, image FROM Post";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, password);
            PreparedStatement stmnt = conn.prepareStatement(query);
            ResultSet rs = stmnt.executeQuery()) {

            while (rs.next()) {
                String imageName = rs.getString("postName");
                byte[] imageBytes = rs.getBytes("image");
                imageDataList.add(new ImageData(imageName, imageBytes));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return imageDataList;
    }
    
    public int getImageCount(String username) {
        String query = "SELECT COUNT(*) AS image_count FROM Post " +
                       "INNER JOIN User ON Post.user_id = User.id " +
                       "WHERE User.name = ?";
        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password);
             PreparedStatement stmnt = connection.prepareStatement(query)) {

            stmnt.setString(1, username);
            try (ResultSet rs = stmnt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("image_count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getNumberOfFollowing(String username) {
        String query = "SELECT COUNT(*) AS following_count FROM userfollows " +
                       "INNER JOIN User ON userfollows.follower_id = User.id " +
                       "WHERE User.name = ?";
        int followingCount = 0;

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password);
             PreparedStatement stmnt = connection.prepareStatement(query)) {

            stmnt.setString(1, username);
            try (ResultSet rs = stmnt.executeQuery()) {
                if (rs.next()) {
                    followingCount = rs.getInt("following_count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return followingCount;
    }

    public String getBio(String username) {
        String query = "SELECT bio FROM User WHERE name = ?";
        String bio = null;

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password);
             PreparedStatement stmnt = connection.prepareStatement(query)) {

            stmnt.setString(1, username);
            try (ResultSet rs = stmnt.executeQuery()) {
                if (rs.next()) {
                    bio = rs.getString("bio");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bio;
    }
    
    public boolean isFollowing(String follower, String followed) {
        String query = "SELECT COUNT(*) AS is_following FROM Follower " +
                       "INNER JOIN User AS FollowerUser ON Follower.follower_id = FollowerUser.id " +
                       "INNER JOIN User AS FollowedUser ON Follower.followed_id = FollowedUser.id " +
                       "WHERE FollowerUser.name = ? AND FollowedUser.name = ?";
        boolean isFollowing = false;

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password);
             PreparedStatement stmnt = connection.prepareStatement(query)) {

            stmnt.setString(1, follower);
            stmnt.setString(2, followed);
            try (ResultSet rs = stmnt.executeQuery()) {
                if (rs.next()) {
                    isFollowing = rs.getInt("is_following") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isFollowing;
    }
    
    public String getProfileImage(String username) {
        String query = "SELECT profile_image FROM User WHERE name = ?";
        String outputDir = "img/store/profile/";
        String outputPath = null;

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password);
            PreparedStatement stmnt = connection.prepareStatement(query)) {

            stmnt.setString(1, username);
            try (ResultSet rs = stmnt.executeQuery()) {
                if (rs.next()) {
                    byte[] imageData = rs.getBytes("profile_image");
                    if (imageData != null) {
                        File dir = new File(outputDir);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        outputPath = outputDir + username + "_profile.jpg";
                        Files.write(Paths.get(outputPath), imageData);
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return outputPath;
    }


    public java.util.List<User> searchUsers(String query) {
        java.util.List<User> users = new java.util.ArrayList<>();
        String sqlQuery = "SELECT name, bio FROM User WHERE LOWER(name) LIKE ?";

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password);
             PreparedStatement stmnt = connection.prepareStatement(sqlQuery)) {

            stmnt.setString(1, "%" + query + "%");
            try (ResultSet rs = stmnt.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("name");
                    String bio = rs.getString("bio");
                    users.add(new User(username, bio, null)); // Assuming password is not needed here
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public java.util.List<String> getUserImages(String username) {
        java.util.List<String> imagePaths = new java.util.ArrayList<>();
        String query = "SELECT Post.postName, Post.image FROM Post " +
                       "INNER JOIN user ON Post.user_id = user.id " +
                       "WHERE user.name = ?";
        String outputDir = "img/store/user_images/";

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password);
             PreparedStatement stmnt = connection.prepareStatement(query)) {

            stmnt.setString(1, username);
            try (ResultSet rs = stmnt.executeQuery()) {
                while (rs.next()) {
                    String imageName = rs.getString("postName");
                    InputStream input = rs.getBinaryStream("image");

                    File dir = new File(outputDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    String destinationPath = Paths.get(outputDir, imageName).toString();
                    Files.copy(input, Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
                    imagePaths.add(destinationPath);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return imagePaths;
    }

    public java.util.List<String> getUserNotifications(String username) {
        java.util.List<String> notifications = new java.util.ArrayList<>();
        String query = "SELECT User.name AS userWhoLiked, Notification.post_id, Notification.dateTime, Notification.type " +
                       "FROM Notification " +
                       "INNER JOIN User ON Notification.user_id = User.id " +
                       "WHERE User.name = ?";

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password);
             PreparedStatement stmnt = connection.prepareStatement(query)) {

            stmnt.setString(1, username);
            try (ResultSet rs = stmnt.executeQuery()) {
                while (rs.next()) {
                    String userWhoLiked = rs.getString("userWhoLiked");
                    String postId = rs.getString("post_id");
                    String timestamp = rs.getString("dateTime");
                    String type = rs.getString("type");

                    String notificationMessage;
                    if ("like".equals(type)) {
                        notificationMessage = userWhoLiked + " liked your post (ID: " + postId + ") - " + getElapsedTime(timestamp) + " ago";
                    } else if ("comment".equals(type)) {
                        notificationMessage = userWhoLiked + " commented on your post (ID: " + postId + ") - " + getElapsedTime(timestamp) + " ago";
                    } else {
                        notificationMessage = "Unknown notification type.";
                    }

                    notifications.add(notificationMessage);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    private String getElapsedTime(String timestamp) {
        // Implement logic to calculate elapsed time from the timestamp to now
        // For simplicity, you can use java.time classes like LocalDateTime and Duration
        java.time.LocalDateTime notificationTime = java.time.LocalDateTime.parse(timestamp.replace(" ", "T"));
        java.time.Duration duration = java.time.Duration.between(notificationTime, java.time.LocalDateTime.now());

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        if (days > 0) {
            return days + " days";
        } else if (hours > 0) {
            return hours + " hours";
        } else {
            return minutes + " minutes";
        }
    }
    
    public void insertNotification(String username, String message) {
        String userIdQuery = "SELECT id FROM User WHERE name = ?";
        String insertQuery = "INSERT INTO Notifications(user_id, message) VALUES (?, ?)";
        String userId = null;

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password)) {
            // Get user ID
            try (PreparedStatement userIdStmnt = connection.prepareStatement(userIdQuery)) {
                userIdStmnt.setString(1, username);
                try (ResultSet rs = userIdStmnt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getString("id");
                    }
                }
            }

            if (userId == null) {
                throw new IllegalArgumentException("User does not exist.");
            }

            // Insert notification
            try (PreparedStatement insertStmnt = connection.prepareStatement(insertQuery)) {
                insertStmnt.setString(1, userId);
                insertStmnt.setString(2, message);
                insertStmnt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getNumberOfLikes(String postId) {
        String query = "SELECT COUNT(*) AS like_count FROM Likes JOIN Post ON Likes.post_id = Post.id WHERE Post.postName = ?";
        int likeCount = 0;

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password);
            PreparedStatement stmnt = connection.prepareStatement(query)) {

            stmnt.setString(1, postId);
            try (ResultSet rs = stmnt.executeQuery()) {
                if (rs.next()) {
                    likeCount = rs.getInt("like_count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return likeCount;
    }

    public void insertLike(String username, String postId) {
        String userIdQuery = "SELECT id FROM User WHERE name = ?";
        String postIdQuery = "SELECT id FROM Post WHERE postName = ?";
        String insertQuery = "INSERT INTO Likes(user_id, post_id) VALUES (?, ?)";
        String userId = null;
        String postDbId = null;

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password)) {
            // Get user ID
            try (PreparedStatement userIdStmnt = connection.prepareStatement(userIdQuery)) {
                userIdStmnt.setString(1, username);
                try (ResultSet rs = userIdStmnt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getString("id");
                    }
                }
            }

            // Get post ID
            try (PreparedStatement postIdStmnt = connection.prepareStatement(postIdQuery)) {
                postIdStmnt.setString(1, postId);
                try (ResultSet rs = postIdStmnt.executeQuery()) {
                    if (rs.next()) {
                        postDbId = rs.getString("id");
                    }
                }
            }

            if (userId == null || postDbId == null) {
                throw new IllegalArgumentException("User or post does not exist.");
            }

            // Insert like
            try (PreparedStatement insertStmnt = connection.prepareStatement(insertQuery)) {
                insertStmnt.setString(1, userId);
                insertStmnt.setString(2, postDbId);
                insertStmnt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public java.util.List<String> getFollowedUsers(String username) {
        java.util.List<String> followedUsers = new java.util.ArrayList<>();
        String query = "SELECT FollowedUser.name FROM Follower " +
                       "INNER JOIN User AS FollowerUser ON Follower.follower_id = FollowerUser.id " +
                       "INNER JOIN User AS FollowedUser ON Follower.followed_id = FollowedUser.id " +
                       "WHERE FollowerUser.name = ?";

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password);
             PreparedStatement stmnt = connection.prepareStatement(query)) {

            stmnt.setString(1, username);
            try (ResultSet rs = stmnt.executeQuery()) {
                while (rs.next()) {
                    followedUsers.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return followedUsers;
    }
    
    public java.util.List<String[]> getPostsByUsers(java.util.List<String> followedUsers) {
        java.util.List<String[]> posts = new java.util.ArrayList<>();
        String query = "SELECT Post.postName, Post.content, User.name AS author " +
                       "FROM Post " +
                       "INNER JOIN User ON Post.user_id = User.id " +
                       "WHERE User.name = ?";

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password)) {
            for (String followedUser : followedUsers) {
                try (PreparedStatement stmnt = connection.prepareStatement(query)) {
                    stmnt.setString(1, followedUser);
                    try (ResultSet rs = stmnt.executeQuery()) {
                        while (rs.next()) {
                            String postName = rs.getString("postName");
                            String content = rs.getString("content");
                            String author = rs.getString("author");
                            posts.add(new String[]{postName, content, author});
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }


}
