package com.aichat.app.controller;

import com.aichat.app.dto.UsageStatusDto;
import com.aichat.app.dto.UserProfileDto;
import com.aichat.app.entity.User;
import com.aichat.app.repository.UserRepository;
import com.aichat.app.security.UserPrincipal;
import com.aichat.app.service.UsageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository;
    private final UsageService usageService;

    public UserController(UserRepository userRepository, UsageService usageService) {
        this.userRepository = userRepository;
        this.usageService = usageService;
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfileDto profile = UserProfileDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .defaultModel(user.getDefaultModel())
                .monthlyMessageCount(user.getMonthlyMessageCount())
                .createdAt(user.getCreatedAt())
                .build();

        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/users/me")
    public ResponseEntity<UserProfileDto> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody Map<String, String> body) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (body.containsKey("name") && body.get("name") != null) {
            user.setName(body.get("name"));
        }
        if (body.containsKey("defaultModel") && body.get("defaultModel") != null) {
            user.setDefaultModel(body.get("defaultModel"));
        }

        user = userRepository.save(user);

        UserProfileDto profile = UserProfileDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .defaultModel(user.getDefaultModel())
                .monthlyMessageCount(user.getMonthlyMessageCount())
                .createdAt(user.getCreatedAt())
                .build();

        return ResponseEntity.ok(profile);
    }

    @GetMapping("/usage")
    public ResponseEntity<UsageStatusDto> getUsage(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return ResponseEntity.ok(usageService.getUsageStatus(user));
    }
}
