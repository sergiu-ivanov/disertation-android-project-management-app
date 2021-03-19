package com.sergiuivanov.finalprojectm.models;

public class GlobalQueue {
    private String queueList, projectID;

    public GlobalQueue(String queueList, String projectID) {
        this.queueList = queueList;
        this.projectID = projectID;
    }

    public String getQueueList() {
        return queueList;
    }

    public void setQueueList(String queueList) {
        this.queueList = queueList;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }
}
