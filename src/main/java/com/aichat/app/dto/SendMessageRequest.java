package com.aichat.app.dto;

import jakarta.validation.constraints.NotBlank;

public class SendMessageRequest {
    @NotBlank(message = "Message content cannot be blank")
    private String content;

    private String model;
    private String attachmentUrl;

    public SendMessageRequest() {}

    public SendMessageRequest(String content, String model, String attachmentUrl) {
        this.content = content;
        this.model = model;
        this.attachmentUrl = attachmentUrl;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
}
