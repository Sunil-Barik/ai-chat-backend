package com.aichat.app.dto;

import java.time.LocalDateTime;

public class ConversationDto {
    private Long id;
    private String title;
    private String model;
    private Boolean isArchived;
    private LocalDateTime createdAt;
    private MessageDto lastMessage;

    public ConversationDto() {}

    public ConversationDto(Long id, String title, String model, Boolean isArchived, LocalDateTime createdAt, MessageDto lastMessage) {
        this.id = id;
        this.title = title;
        this.model = model;
        this.isArchived = isArchived;
        this.createdAt = createdAt;
        this.lastMessage = lastMessage;
    }

    public static ConversationDtoBuilder builder() {
        return new ConversationDtoBuilder();
    }

    public static class ConversationDtoBuilder {
        private Long id;
        private String title;
        private String model;
        private Boolean isArchived;
        private LocalDateTime createdAt;
        private MessageDto lastMessage;

        public ConversationDtoBuilder id(Long id) { this.id = id; return this; }
        public ConversationDtoBuilder title(String title) { this.title = title; return this; }
        public ConversationDtoBuilder model(String model) { this.model = model; return this; }
        public ConversationDtoBuilder isArchived(Boolean isArchived) { this.isArchived = isArchived; return this; }
        public ConversationDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ConversationDtoBuilder lastMessage(MessageDto lastMessage) { this.lastMessage = lastMessage; return this; }

        public ConversationDto build() {
            return new ConversationDto(id, title, model, isArchived, createdAt, lastMessage);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Boolean getIsArchived() { return isArchived; }
    public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public MessageDto getLastMessage() { return lastMessage; }
    public void setLastMessage(MessageDto lastMessage) { this.lastMessage = lastMessage; }
}
