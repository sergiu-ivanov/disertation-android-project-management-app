package com.sergiuivanov.finalprojectm.models;

import java.util.HashMap;
import java.util.Map;

public class ProjectBooked {
    private String author, description, title , email, status, key, id, queuePosition;

    public ProjectBooked(String key, String author, String description, String title, String email, String status, String id, String queuePosition) {
        this.author = author;
        this.description = description;
        this.title = title;
        this.email = email;
        this.status = status;
        this.key = key;
        this.id = id;
        this.queuePosition = queuePosition;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(String queuePosition) {
        this.queuePosition = queuePosition;
    }

    public String getId() {
        return id;
    }

    public ProjectBooked() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("title", title);
        result.put("author", author);
        result.put("description", description);
        result.put("email", email);
        result.put("status", status);
        result.put("key", key);

        return result;
    }
}
