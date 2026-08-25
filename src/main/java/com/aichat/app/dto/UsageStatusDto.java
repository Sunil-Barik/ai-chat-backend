package com.aichat.app.dto;

public class UsageStatusDto {
    private Integer dailyMessagesUsed;
    private Integer dailyLimit;
    private Integer monthlyMessagesUsed;
    private Integer monthlyLimit;
    private Boolean isQuotaExceeded;

    public UsageStatusDto() {}

    public UsageStatusDto(Integer dailyMessagesUsed, Integer dailyLimit, Integer monthlyMessagesUsed, Integer monthlyLimit, Boolean isQuotaExceeded) {
        this.dailyMessagesUsed = dailyMessagesUsed;
        this.dailyLimit = dailyLimit;
        this.monthlyMessagesUsed = monthlyMessagesUsed;
        this.monthlyLimit = monthlyLimit;
        this.isQuotaExceeded = isQuotaExceeded;
    }

    public static UsageStatusDtoBuilder builder() {
        return new UsageStatusDtoBuilder();
    }

    public static class UsageStatusDtoBuilder {
        private Integer dailyMessagesUsed;
        private Integer dailyLimit;
        private Integer monthlyMessagesUsed;
        private Integer monthlyLimit;
        private Boolean isQuotaExceeded;

        public UsageStatusDtoBuilder dailyMessagesUsed(Integer dailyMessagesUsed) { this.dailyMessagesUsed = dailyMessagesUsed; return this; }
        public UsageStatusDtoBuilder dailyLimit(Integer dailyLimit) { this.dailyLimit = dailyLimit; return this; }
        public UsageStatusDtoBuilder monthlyMessagesUsed(Integer monthlyMessagesUsed) { this.monthlyMessagesUsed = monthlyMessagesUsed; return this; }
        public UsageStatusDtoBuilder monthlyLimit(Integer monthlyLimit) { this.monthlyLimit = monthlyLimit; return this; }
        public UsageStatusDtoBuilder isQuotaExceeded(Boolean isQuotaExceeded) { this.isQuotaExceeded = isQuotaExceeded; return this; }

        public UsageStatusDto build() {
            return new UsageStatusDto(dailyMessagesUsed, dailyLimit, monthlyMessagesUsed, monthlyLimit, isQuotaExceeded);
        }
    }

    public Integer getDailyMessagesUsed() { return dailyMessagesUsed; }
    public void setDailyMessagesUsed(Integer dailyMessagesUsed) { this.dailyMessagesUsed = dailyMessagesUsed; }

    public Integer getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(Integer dailyLimit) { this.dailyLimit = dailyLimit; }

    public Integer getMonthlyMessagesUsed() { return monthlyMessagesUsed; }
    public void setMonthlyMessagesUsed(Integer monthlyMessagesUsed) { this.monthlyMessagesUsed = monthlyMessagesUsed; }

    public Integer getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(Integer monthlyLimit) { this.monthlyLimit = monthlyLimit; }

    public Boolean getIsQuotaExceeded() { return isQuotaExceeded; }
    public void setIsQuotaExceeded(Boolean isQuotaExceeded) { this.isQuotaExceeded = isQuotaExceeded; }
}
