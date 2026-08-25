package com.aichat.app.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "usage_logs")
public class UsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "message_count")
    private Integer messageCount = 0;

    @Column(name = "token_count")
    private Integer tokenCount = 0;

    public UsageLog() {}

    public UsageLog(Long id, User user, LocalDate date, Integer messageCount, Integer tokenCount) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.messageCount = messageCount != null ? messageCount : 0;
        this.tokenCount = tokenCount != null ? tokenCount : 0;
    }

    public static UsageLogBuilder builder() {
        return new UsageLogBuilder();
    }

    public static class UsageLogBuilder {
        private Long id;
        private User user;
        private LocalDate date;
        private Integer messageCount = 0;
        private Integer tokenCount = 0;

        public UsageLogBuilder id(Long id) { this.id = id; return this; }
        public UsageLogBuilder user(User user) { this.user = user; return this; }
        public UsageLogBuilder date(LocalDate date) { this.date = date; return this; }
        public UsageLogBuilder messageCount(Integer messageCount) { this.messageCount = messageCount; return this; }
        public UsageLogBuilder tokenCount(Integer tokenCount) { this.tokenCount = tokenCount; return this; }

        public UsageLog build() {
            return new UsageLog(id, user, date, messageCount, tokenCount);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getMessageCount() { return messageCount; }
    public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }

    public Integer getTokenCount() { return tokenCount; }
    public void setTokenCount(Integer tokenCount) { this.tokenCount = tokenCount; }
}
