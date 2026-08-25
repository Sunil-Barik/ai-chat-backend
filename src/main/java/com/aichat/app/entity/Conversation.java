package com.aichat.app.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(name = "model")
    private String model = "gemini-2.0-flash";

    @Column(name = "is_archived")
    private Boolean isArchived = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Conversation() {}

    public Conversation(Long id, User user, String title, String model, Boolean isArchived, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.model = model != null ? model : "gemini-2.0-flash";
        this.isArchived = isArchived != null ? isArchived : false;
        this.createdAt = createdAt;
    }

    public static ConversationBuilder builder() {
        return new ConversationBuilder();
    }

    public static class ConversationBuilder {
        private Long id;
        private User user;
        private String title;
        private String model = "gemini-2.0-flash";
        private Boolean isArchived = false;
        private LocalDateTime createdAt;

        public ConversationBuilder id(Long id) { this.id = id; return this; }
        public ConversationBuilder user(User user) { this.user = user; return this; }
        public ConversationBuilder title(String title) { this.title = title; return this; }
        public ConversationBuilder model(String model) { this.model = model; return this; }
        public ConversationBuilder isArchived(Boolean isArchived) { this.isArchived = isArchived; return this; }
        public ConversationBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Conversation build() {
            return new Conversation(id, user, title, model, isArchived, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Boolean getIsArchived() { return isArchived; }
    public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
