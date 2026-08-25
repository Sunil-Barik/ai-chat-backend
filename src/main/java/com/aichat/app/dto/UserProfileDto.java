package com.aichat.app.dto;

import java.time.LocalDateTime;

public class UserProfileDto {
    private Long id;
    private String name;
    private String email;
    private String defaultModel;
    private Integer monthlyMessageCount;
    private LocalDateTime createdAt;

    public UserProfileDto() {}

    public UserProfileDto(Long id, String name, String email, String defaultModel, Integer monthlyMessageCount, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.defaultModel = defaultModel;
        this.monthlyMessageCount = monthlyMessageCount;
        this.createdAt = createdAt;
    }

    public static UserProfileDtoBuilder builder() {
        return new UserProfileDtoBuilder();
    }

    public static class UserProfileDtoBuilder {
        private Long id;
        private String name;
        private String email;
        private String defaultModel;
        private Integer monthlyMessageCount;
        private LocalDateTime createdAt;

        public UserProfileDtoBuilder id(Long id) { this.id = id; return this; }
        public UserProfileDtoBuilder name(String name) { this.name = name; return this; }
        public UserProfileDtoBuilder email(String email) { this.email = email; return this; }
        public UserProfileDtoBuilder defaultModel(String defaultModel) { this.defaultModel = defaultModel; return this; }
        public UserProfileDtoBuilder monthlyMessageCount(Integer monthlyMessageCount) { this.monthlyMessageCount = monthlyMessageCount; return this; }
        public UserProfileDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public UserProfileDto build() {
            return new UserProfileDto(id, name, email, defaultModel, monthlyMessageCount, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDefaultModel() { return defaultModel; }
    public void setDefaultModel(String defaultModel) { this.defaultModel = defaultModel; }

    public Integer getMonthlyMessageCount() { return monthlyMessageCount; }
    public void setMonthlyMessageCount(Integer monthlyMessageCount) { this.monthlyMessageCount = monthlyMessageCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
