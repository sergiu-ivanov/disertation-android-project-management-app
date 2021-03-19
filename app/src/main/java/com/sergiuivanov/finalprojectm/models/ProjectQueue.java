package com.sergiuivanov.finalprojectm.models;


public class ProjectQueue {

    private String queueList, projectID;

    public ProjectQueue() {

    }

    public ProjectQueue(String queueList, String projectID) {
        this.queueList = queueList;
        this.projectID = projectID;

    }

    public String getQueueList() {
        return queueList;
    }

    public void setQueueList(String queue) {
        this.queueList = queueList;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }
}