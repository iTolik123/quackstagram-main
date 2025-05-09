package com.example;

import java.io.IOException;
import java.util.List;

public class UserRelationshipManager {

    private final DbManager dbManager;

    public UserRelationshipManager() {
        this.dbManager = new DbManager();
    }

    // Method to follow a user
    public void followUser(String follower, String followed) throws IOException {
        if (!dbManager.isFollowing(follower, followed)) {
            dbManager.insertFollower(follower, followed);
        }
    }

    // Method to get the list of followers for a user
    public List<String> getFollowers(String username) throws IOException {
        return dbManager.getFollowedUsers(username);
    }

    // Method to get the list of users a user is following
    public List<String> getFollowing(String username) throws IOException {
        return dbManager.getFollowedUsers(username);
    }
}
