package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    private final String password = "password";

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
}
