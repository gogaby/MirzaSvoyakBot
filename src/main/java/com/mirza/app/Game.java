package com.mirza.app;

import com.mirza.model.TopicSet;
import com.mirza.model.User;

import java.util.Map;

public class Game {

    Map<Integer,User> registeredUsers;
    TopicSet topicSet;
    Map<Integer, Integer> score;

    public Game(Map<Integer, User> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }
}