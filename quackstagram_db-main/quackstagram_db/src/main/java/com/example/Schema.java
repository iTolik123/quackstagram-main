package com.example;

import java.sql.*;

public class Schema {
    private final String dbUrl = "jdbc:mysql://localhost:3306/quackstagram";
    private final String dbUsername = "root";
    private  final String password = "";

    private void Connectingdb() {
        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password)) {
            Statement stmnt = connection.createStatement();
            String query = "CREATE TABLE User (\n" + //
                                "    id INT PRIMARY KEY,\n" + //
                                "    name VARCHAR(100) NOT NULL,\n" + //
                                "    password VARCHAR(100) NOT NULL\n" + //
                                ");\n" + //
                                "\n" + //
                                "-- Self-referencing relationship for User following another User\n" + //
                                "CREATE TABLE UserFollows (\n" + //
                                "    follower_id INT,\n" + //
                                "    followed_id INT,\n" + //
                                "    PRIMARY KEY (follower_id, followed_id),\n" + //
                                "    FOREIGN KEY (follower_id) REFERENCES User(id),\n" + //
                                "    FOREIGN KEY (followed_id) REFERENCES User(id)\n" + //
                                ");\n" + //
                                "\n" + //
                                "CREATE TABLE Post (\n" + //
                                "    id INT PRIMARY KEY AUTO_INCREMENT,\n" + //
                                "    user_id INT NOT NULL,\n" + //
                                "    bio TEXT,\n" + //
                                "    image VARCHAR(255),\n" + //
                                "    FOREIGN KEY (user_id) REFERENCES User(id)\n" + //
                                ");\n" + //
                                "\n" + //
                                "CREATE TABLE Like⁠ (\n" + //
                                "    user_id INT,\n" + //
                                "    post_id INT,\n" + //
                                "    PRIMARY KEY (user_id, post_id),\n" + //
                                "    FOREIGN KEY (user_id) REFERENCES User(id),\n" + //
                                "    FOREIGN KEY (post_id) REFERENCES Post(id)\n" + //
                                ");\n" + //
                                "\n" + //
                                "CREATE TABLE Notification (\n" + //
                                "    id INT PRIMARY KEY AUTO_INCREMENT,\n" + //
                                "    post_id INT NOT NULL,\n" + //
                                "    dateTime DATETIME NOT NULL,\n" + //
                                "    FOREIGN KEY (post_id) REFERENCES Post(id)\n" + //
                                ");";
                stmnt.executeQuery(query);

        } catch (SQLException e) {
            
            e.printStackTrace();
        }
            
    }

}
