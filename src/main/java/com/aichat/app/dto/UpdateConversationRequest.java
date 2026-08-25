package com.aichat.app.dto;

public class UpdateConversationRequest {
    private String title;
    private String model;
    private Boolean isArchived;

    public UpdateConversationRequest() {}

    public UpdateConversationRequest(String title, String model, Boolean isArchived) {
        this.title = title;
        this.model = model;
        this.isArchived = isArchived;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Boolean getIsArchived() { return isArchived; }
    public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }
}
