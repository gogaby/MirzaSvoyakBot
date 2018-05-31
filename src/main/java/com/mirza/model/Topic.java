package com.mirza.model;

import java.util.List;
import java.util.UUID;

/**
 * Created by yach0217 on 29.05.2018.
 */
public class Topic {
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    private UUID id;

    private String topicName;
    private String author;
    private List<Question> questions;
    private UUID packageId;

    public UUID getPackageId() {
        return packageId;
    }

    public void setPackageId(UUID packageId) {
        this.packageId = packageId;
    }

    public Topic(String topicName, List<Question> questions) {
        this.topicName = topicName;
        this.questions = questions;
    }

    public Topic() {

    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public static String getTopicWord(int topics) {
        if (topics % 10 == 0 || topics % 10 >= 5 || topics % 100 >= 10 && topics % 100 < 20) {
            return "тем";
        }
        if (topics % 10 == 1) {
            return "тема";
        }
        return "темы";
    }
}