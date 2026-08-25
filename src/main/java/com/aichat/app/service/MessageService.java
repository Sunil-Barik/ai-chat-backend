package com.aichat.app.service;

import com.aichat.app.dto.MessageDto;
import com.aichat.app.dto.SendMessageRequest;
import com.aichat.app.entity.Conversation;
import com.aichat.app.entity.Message;
import com.aichat.app.entity.Sender;
import com.aichat.app.entity.User;
import com.aichat.app.repository.ConversationRepository;
import com.aichat.app.repository.MessageRepository;
import com.aichat.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final AiService aiService;
    private final UsageService usageService;

    public MessageService(MessageRepository messageRepository, ConversationRepository conversationRepository, UserRepository userRepository, AiService aiService, UsageService usageService) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.aiService = aiService;
        this.usageService = usageService;
    }

    public List<MessageDto> getMessagesByConversation(Long userId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!conversation.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized access to conversation");
        }

        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDto sendMessage(Long userId, Long conversationId, SendMessageRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!conversation.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized access to conversation");
        }

        if (request.getModel() != null && !request.getModel().isBlank()) {
            conversation.setModel(request.getModel());
        }

        // 1. Save User Message
        Message userMessage = Message.builder()
                .conversation(conversation)
                .sender(Sender.USER)
                .content(request.getContent())
                .attachmentUrl(request.getAttachmentUrl())
                .tokenCount(estimateTokens(request.getContent()))
                .build();

        messageRepository.save(userMessage);

        // Update conversation title if default title
        if ("New Conversation".equals(conversation.getTitle())) {
            conversation.setTitle(aiService.generateTitle(request.getContent()));
        }
        conversationRepository.save(conversation);

        // 2. Fetch history for AI prompt context
        List<Message> history = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        // 3. Generate AI response
        String aiReplyContent = aiService.generateReply(history, conversation.getModel());

        // 4. Save AI Message
        Message aiMessage = Message.builder()
                .conversation(conversation)
                .sender(Sender.AI)
                .content(aiReplyContent)
                .tokenCount(estimateTokens(aiReplyContent))
                .build();

        aiMessage = messageRepository.save(aiMessage);

        // 5. Record usage metrics
        usageService.recordUsage(user, (userMessage.getTokenCount() + aiMessage.getTokenCount()));

        return mapToDto(aiMessage);
    }

    public MessageDto mapToDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .sender(message.getSender())
                .content(message.getContent())
                .attachmentUrl(message.getAttachmentUrl())
                .tokenCount(message.getTokenCount())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private int estimateTokens(String text) {
        if (text == null || text.isBlank()) return 0;
        return (int) Math.ceil(text.length() / 4.0);
    }
}
