package com.example;

import java.sql.*;

public class Schema {
    public static void main(String[] args) {
        String dbUrl = "jdbc:mysql://localhost:3306/quackstagram";
        String dbUsername = "root";
        String password = ""; 

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, password)) {
            Statement stmnt = connection.createStatement();

            stmnt.execute("CREATE TABLE IF NOT EXISTS User (" +
                    "id INT PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "password VARCHAR(100) NOT NULL" +
                    ");");

            stmnt.execute("CREATE TABLE IF NOT EXISTS UserFollows (" +
                    "follower_id INT," +
                    "followed_id INT," +
                    "PRIMARY KEY (follower_id, followed_id)," +
                    "FOREIGN KEY (follower_id) REFERENCES User(id)," +
                    "FOREIGN KEY (followed_id) REFERENCES User(id)" +
                    ");");

            stmnt.execute("CREATE TABLE IF NOT EXISTS Post (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT NOT NULL," +
                    "bio TEXT," +
                    "image VARCHAR(255)," +
                    "FOREIGN KEY (user_id) REFERENCES User(id)" +
                    ");");

            stmnt.execute("CREATE TABLE IF NOT EXISTS `Like` (" +  // "Like" is a reserved word in SQL
                    "user_id INT," +
                    "post_id INT," +
                    "PRIMARY KEY (user_id, post_id)," +
                    "FOREIGN KEY (user_id) REFERENCES User(id)," +
                    "FOREIGN KEY (post_id) REFERENCES Post(id)" +
                    ");");

            stmnt.execute("CREATE TABLE IF NOT EXISTS Notification (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "post_id INT NOT NULL," +
                    "dateTime DATETIME NOT NULL," +
                    "FOREIGN KEY (post_id) REFERENCES Post(id)" +
                    ");");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
