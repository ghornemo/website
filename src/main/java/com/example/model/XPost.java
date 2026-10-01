package com.example.model;

public class XPost {

    private String id;
    private String text;
    private String fromUser;
    private String createdAt;

    public XPost() {
    }

    public XPost(String id, String text, String fromUser, String createdAt) {
        this.id = id;
        this.text = text;
        this.fromUser = fromUser;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}