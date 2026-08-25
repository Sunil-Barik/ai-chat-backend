package com.aichat.app.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "default_model")
    private String defaultModel = "gemini-2.0-flash";

    @Column(name = "monthly_message_count")
    private Integer monthlyMessageCount = 0;

    @Column(name = "monthly_reset_at")
    private LocalDateTime monthlyResetAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public User() {}

    public User(Long id, String name, String email, String passwordHash, String defaultModel, Integer monthlyMessageCount, LocalDateTime monthlyResetAt, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.defaultModel = defaultModel != null ? defaultModel : "gemini-2.0-flash";
        this.monthlyMessageCount = monthlyMessageCount != null ? monthlyMessageCount : 0;
        this.monthlyResetAt = monthlyResetAt;
        this.createdAt = createdAt;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long id;
        private String name;
        private String email;
        private String passwordHash;
        private String defaultModel = "gemini-2.0-flash";
        private Integer monthlyMessageCount = 0;
        private LocalDateTime monthlyResetAt;
        private LocalDateTime createdAt;

        public UserBuilder id(Long id) { this.id = id; return this; }
        public UserBuilder name(String name) { this.name = name; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public UserBuilder defaultModel(String defaultModel) { this.defaultModel = defaultModel; return this; }
        public UserBuilder monthlyMessageCount(Integer monthlyMessageCount) { this.monthlyMessageCount = monthlyMessageCount; return this; }
        public UserBuilder monthlyResetAt(LocalDateTime monthlyResetAt) { this.monthlyResetAt = monthlyResetAt; return this; }
        public UserBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public User build() {
            return new User(id, name, email, passwordHash, defaultModel, monthlyMessageCount, monthlyResetAt, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getDefaultModel() { return defaultModel; }
    public void setDefaultModel(String defaultModel) { this.defaultModel = defaultModel; }

    public Integer getMonthlyMessageCount() { return monthlyMessageCount; }
    public void setMonthlyMessageCount(Integer monthlyMessageCount) { this.monthlyMessageCount = monthlyMessageCount; }

    public LocalDateTime getMonthlyResetAt() { return monthlyResetAt; }
    public void setMonthlyResetAt(LocalDateTime monthlyResetAt) { this.monthlyResetAt = monthlyResetAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
