package com.ericsson.nms.rv.taf.test.apache.operators;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserPool {
    private static Logger logger = LoggerFactory.getLogger(UserPool.class);

    private static Map<String, User> userMap = new ConcurrentHashMap<String, User>();

    /**
     * Add a user to the userPool
     * @param userID - The user ID of the user to be returned
     * @param user - The user which is being added to the pool
     */
    public static void addUser(String userID, User user) {
        logger.info("Adding user {} into map.",userID);
        userMap.put(userID, user);
    }

    /**
     * Returns a User from the User Pool with specified ID
     * @param userID - The user ID of the user to be returned
     * @return User from pool
     */
    public static User getUserByID(String userID) {
        User user = null;
        logger.info("Searching for user ID {} in user pool", userID);
        if (userMap.containsKey(userID)) {
            user = userMap.get(userID);
        } else {
            logger.error("User {} does not exist in the user pool");
        }
        logger.info("Found user {}", userID);
        return user;
    }

    /**
     * Returns a list of all the users in the user pool
     * @return List of all users
     */
    public static List<User> getAllUsers() {
        final List<User> toReturn = new ArrayList<User>();
        for (final String uid : userMap.keySet()) {
            toReturn.add(userMap.get(uid));
        }
        return toReturn;
    }
}