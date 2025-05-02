package com.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class Schema {
        private final String dbUrl = "jdbc:mysql://localhost:3306/quackstagram";
        private final String dbUsername = "root";
        private final String password = ""; 
        private User newuser;
    public Schema() {

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, password)) {
            Statement stmnt = connection.createStatement();

            stmnt.execute("CREATE TABLE IF NOT EXISTS User (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
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
    public boolean verifyCredentials(String username , String password){
        String query = "SELECT * FROM User WHERE User.name = ? AND User.password = ?";
        String check_username = "";
        String check_password = "";

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.password)) {
                PreparedStatement stmnt = connection.prepareStatement(query);
                stmnt.setString(1,username);
                stmnt.setString(2, password);
                ResultSet rs = stmnt.executeQuery();
                while (rs.next()) {
                        
                        check_username = rs.getString(2);
                        check_password = rs.getString(3);
                }
        } catch (SQLException e) {
                e.printStackTrace();
        }
        if(check_password.equals(password) && check_username.equals(username)){
                String bio = "";
                this.newuser = new User(username, bio, password); // Assuming User constructor takes these parameters
                 saveUserInformation(this.newuser); 
                return true;
        }
        return false;
}
   private void saveUserInformation(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("quackstagram_db-main/quackstagram_db/src/data/users.txt", false))) {
            writer.write(user.toString());  // Implement a suitable toString method in User class
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
