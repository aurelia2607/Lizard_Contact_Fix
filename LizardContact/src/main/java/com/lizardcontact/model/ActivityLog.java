package com.lizardcontact.model;

import java.time.LocalDateTime;

public class ActivityLog {
    private int logID;
    private String action;
    private String contactName;
    private String description;
    private LocalDateTime timestamp;
    private int userID;

    public ActivityLog() {}

    public ActivityLog(String action, String contactName, String description) {
        this.action = action;
        this.contactName = contactName;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public int getLogID() { return logID; }
    public void setLogID(int logID) { this.logID = logID; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }
}
