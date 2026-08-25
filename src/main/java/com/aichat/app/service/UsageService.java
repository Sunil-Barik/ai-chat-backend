package com.aichat.app.service;

import com.aichat.app.dto.UsageStatusDto;
import com.aichat.app.entity.UsageLog;
import com.aichat.app.entity.User;
import com.aichat.app.repository.UsageLogRepository;
import com.aichat.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UsageService {

    private final UsageLogRepository usageLogRepository;
    private final UserRepository userRepository;

    private static final int DAILY_LIMIT = 500;
    private static final int MONTHLY_LIMIT = 10000;

    public UsageService(UsageLogRepository usageLogRepository, UserRepository userRepository) {
        this.usageLogRepository = usageLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void recordUsage(User user, int tokensEstimated) {
        LocalDate today = LocalDate.now();
        UsageLog log = usageLogRepository.findByUserIdAndDate(user.getId(), today)
                .orElseGet(() -> UsageLog.builder()
                        .user(user)
                        .date(today)
                        .messageCount(0)
                        .tokenCount(0)
                        .build());

        log.setMessageCount(log.getMessageCount() + 1);
        log.setTokenCount(log.getTokenCount() + tokensEstimated);
        usageLogRepository.save(log);

        user.setMonthlyMessageCount((user.getMonthlyMessageCount() == null ? 0 : user.getMonthlyMessageCount()) + 1);
        userRepository.save(user);
    }

    public UsageStatusDto getUsageStatus(User user) {
        LocalDate today = LocalDate.now();
        UsageLog log = usageLogRepository.findByUserIdAndDate(user.getId(), today).orElse(null);
        int dailyUsed = log != null ? log.getMessageCount() : 0;
        int monthlyUsed = user.getMonthlyMessageCount() != null ? user.getMonthlyMessageCount() : 0;

        boolean exceeded = dailyUsed >= DAILY_LIMIT || monthlyUsed >= MONTHLY_LIMIT;

        return UsageStatusDto.builder()
                .dailyMessagesUsed(dailyUsed)
                .dailyLimit(DAILY_LIMIT)
                .monthlyMessagesUsed(monthlyUsed)
                .monthlyLimit(MONTHLY_LIMIT)
                .isQuotaExceeded(exceeded)
                .build();
    }
}
