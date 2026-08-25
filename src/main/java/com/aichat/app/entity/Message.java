package com.aichat.app.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sender sender;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "token_count")
    private Integer tokenCount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Message() {}

    public Message(Long id, Conversation conversation, Sender sender, String content, String attachmentUrl, Integer tokenCount, LocalDateTime createdAt) {
        this.id = id;
        this.conversation = conversation;
        this.sender = sender;
        this.content = content;
        this.attachmentUrl = attachmentUrl;
        this.tokenCount = tokenCount;
        this.createdAt = createdAt;
    }

    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public static class MessageBuilder {
        private Long id;
        private Conversation conversation;
        private Sender sender;
        private String content;
        private String attachmentUrl;
        private Integer tokenCount;
        private LocalDateTime createdAt;

        public MessageBuilder id(Long id) { this.id = id; return this; }
        public MessageBuilder conversation(Conversation conversation) { this.conversation = conversation; return this; }
        public MessageBuilder sender(Sender sender) { this.sender = sender; return this; }
        public MessageBuilder content(String content) { this.content = content; return this; }
        public MessageBuilder attachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; return this; }
        public MessageBuilder tokenCount(Integer tokenCount) { this.tokenCount = tokenCount; return this; }
        public MessageBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Message build() {
            return new Message(id, conversation, sender, content, attachmentUrl, tokenCount, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Conversation getConversation() { return conversation; }
    public void setConversation(Conversation conversation) { this.conversation = conversation; }

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
