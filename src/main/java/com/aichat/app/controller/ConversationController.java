package com.aichat.app.controller;

import com.aichat.app.dto.ConversationDto;
import com.aichat.app.dto.MessageDto;
import com.aichat.app.dto.SendMessageRequest;
import com.aichat.app.dto.UpdateConversationRequest;
import com.aichat.app.security.UserPrincipal;
import com.aichat.app.service.ConversationService;
import com.aichat.app.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConversationController {

    private final ConversationService conversationService;
    private final MessageService messageService;

    public ConversationController(ConversationService conversationService, MessageService messageService) {
        this.conversationService = conversationService;
        this.messageService = messageService;
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDto>> getConversations(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false, defaultValue = "false") Boolean includeArchived) {
        return ResponseEntity.ok(conversationService.getUserConversations(userPrincipal.getId(), includeArchived));
    }

    @PostMapping("/conversations")
    public ResponseEntity<ConversationDto> createConversation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody(required = false) Map<String, String> body) {
        String title = body != null ? body.get("title") : null;
        String model = body != null ? body.get("model") : null;
        return ResponseEntity.ok(conversationService.createConversation(userPrincipal.getId(), title, model));
    }

    @PatchMapping("/conversations/{id}")
    public ResponseEntity<ConversationDto> updateConversation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @RequestBody UpdateConversationRequest request) {
        return ResponseEntity.ok(conversationService.updateConversation(userPrincipal.getId(), id, request));
    }

    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<Map<String, String>> deleteConversation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        conversationService.deleteConversation(userPrincipal.getId(), id);
        return ResponseEntity.ok(Map.of("message", "Conversation deleted successfully"));
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        return ResponseEntity.ok(messageService.getMessagesByConversation(userPrincipal.getId(), id));
    }

    @PostMapping("/conversations/{id}/messages")
    public ResponseEntity<MessageDto> sendMessage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(messageService.sendMessage(userPrincipal.getId(), id, request));
    }

    @PostMapping(value = "/conversations/{id}/messages/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMessage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody SendMessageRequest request) {
        return conversationService.streamMessage(userPrincipal.getId(), id, request);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ConversationDto>> searchConversations(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("q") String query) {
        return ResponseEntity.ok(conversationService.searchConversations(userPrincipal.getId(), query));
    }
}
