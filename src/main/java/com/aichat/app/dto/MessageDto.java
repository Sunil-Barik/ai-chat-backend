package com.aichat.app.dto;

import com.aichat.app.entity.Sender;

import java.time.LocalDateTime;

public class MessageDto {
    private Long id;
    private Long conversationId;
    private Sender sender;
    private String content;
    private String attachmentUrl;
    private Integer tokenCount;
    private LocalDateTime createdAt;

    public MessageDto() {}

    public MessageDto(Long id, Long conversationId, Sender sender, String content, String attachmentUrl, Integer tokenCount, LocalDateTime createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.sender = sender;
        this.content = content;
        this.attachmentUrl = attachmentUrl;
        this.tokenCount = tokenCount;
        this.createdAt = createdAt;
    }

    public static MessageDtoBuilder builder() {
        return new MessageDtoBuilder();
    }

    public static class MessageDtoBuilder {
        private Long id;
        private Long conversationId;
        private Sender sender;
        private String content;
        private String attachmentUrl;
        private Integer tokenCount;
        private LocalDateTime createdAt;

        public MessageDtoBuilder id(Long id) { this.id = id; return this; }
        public MessageDtoBuilder conversationId(Long conversationId) { this.conversationId = conversationId; return this; }
        public MessageDtoBuilder sender(Sender sender) { this.sender = sender; return this; }
        public MessageDtoBuilder content(String content) { this.content = content; return this; }
        public MessageDtoBuilder attachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; return this; }
        public MessageDtoBuilder tokenCount(Integer tokenCount) { this.tokenCount = tokenCount; return this; }
        public MessageDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public MessageDto build() {
            return new MessageDto(id, conversationId, sender, content, attachmentUrl, tokenCount, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public Sender getSender() { return sender; }
    public void setSender(Sender sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public Integer getTokenCount() { return tokenCount; }
    public void setTokenCount(Integer tokenCount) { this.tokenCount = tokenCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
