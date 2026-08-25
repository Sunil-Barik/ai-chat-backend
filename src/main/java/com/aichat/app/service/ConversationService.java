package com.aichat.app.service;

import com.aichat.app.dto.ConversationDto;
import com.aichat.app.dto.MessageDto;
import com.aichat.app.dto.SendMessageRequest;
import com.aichat.app.dto.UpdateConversationRequest;
import com.aichat.app.entity.Conversation;
import com.aichat.app.entity.Message;
import com.aichat.app.entity.Sender;
import com.aichat.app.entity.User;
import com.aichat.app.repository.ConversationRepository;
import com.aichat.app.repository.MessageRepository;
import com.aichat.app.repository.UserRepository;
import util.ChatSseEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private static final Logger log = LoggerFactory.getLogger(ConversationService.class);

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final AiService aiService;
    private final UsageService usageService;

    public ConversationService(ConversationRepository conversationRepository, MessageRepository messageRepository, UserRepository userRepository, MessageService messageService, AiService aiService, UsageService usageService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageService = messageService;
        this.aiService = aiService;
        this.usageService = usageService;
    }

    public List<ConversationDto> getUserConversations(Long userId, Boolean includeArchived) {
        List<Conversation> conversations;
        if (Boolean.TRUE.equals(includeArchived)) {
            conversations = conversationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } else {
            conversations = conversationRepository.findByUserIdAndIsArchivedOrderByCreatedAtDesc(userId, false);
        }

        return conversations.stream().map(c -> {
            List<Message> msgs = messageRepository.findByConversationIdOrderByCreatedAtAsc(c.getId());
            MessageDto lastMsg = msgs.isEmpty() ? null : messageService.mapToDto(msgs.get(msgs.size() - 1));
            return mapToDto(c, lastMsg);
        }).collect(Collectors.toList());
    }

    @Transactional
    public ConversationDto createConversation(Long userId, String initialTitle, String model) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String selectedModel = (model != null && !model.isBlank()) ? model : user.getDefaultModel();
        String title = (initialTitle != null && !initialTitle.isBlank()) ? initialTitle : "New Conversation";

        Conversation conversation = Conversation.builder()
                .user(user)
                .title(title)
                .model(selectedModel)
                .isArchived(false)
                .build();

        conversation = conversationRepository.save(conversation);
        return mapToDto(conversation, null);
    }

    @Transactional
    public ConversationDto updateConversation(Long userId, Long conversationId, UpdateConversationRequest request) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!conversation.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized access to conversation");
        }

        if (request.getTitle() != null) {
            conversation.setTitle(request.getTitle());
        }
        if (request.getModel() != null) {
            conversation.setModel(request.getModel());
        }
        if (request.getIsArchived() != null) {
            conversation.setIsArchived(request.getIsArchived());
        }

        conversation = conversationRepository.save(conversation);
        return mapToDto(conversation, null);
    }

    @Transactional
    public void deleteConversation(Long userId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!conversation.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized access to conversation");
        }

        messageRepository.deleteByConversationId(conversationId);
        conversationRepository.delete(conversation);
    }

    public List<ConversationDto> searchConversations(Long userId, String query) {
        if (query == null || query.isBlank()) {
            return getUserConversations(userId, false);
        }
        return conversationRepository.searchConversations(userId, query).stream()
                .map(c -> mapToDto(c, null))
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatSseEmitter streamMessage(Long userId, Long conversationId, SendMessageRequest request) {
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

        Message userMessage = Message.builder()
                .conversation(conversation)
                .sender(Sender.USER)
                .content(request.getContent())
                .attachmentUrl(request.getAttachmentUrl())
                .tokenCount(estimateTokens(request.getContent()))
                .build();
        messageRepository.save(userMessage);

        if ("New Conversation".equals(conversation.getTitle())) {
            conversation.setTitle(aiService.generateTitle(request.getContent()));
        }
        conversationRepository.save(conversation);

        List<Message> history = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        ChatSseEmitter customEmitter = new ChatSseEmitter(180000L);

        // Guards against saving the AI reply twice, in case both the
        // synchronous path below AND a later async callback (onTimeout/
        // onError) try to persist it.
        AtomicBoolean saved = new AtomicBoolean(false);

        Runnable persistAiReply = () -> {
            if (!saved.compareAndSet(false, true)) {
                return;
            }
            String fullReply = customEmitter.getFullText();
            if (fullReply != null && !fullReply.isBlank()) {
                Message aiMessage = Message.builder()
                        .conversation(conversation)
                        .sender(Sender.AI)
                        .content(fullReply)
                        .tokenCount(estimateTokens(fullReply))
                        .build();
                messageRepository.save(aiMessage);
                usageService.recordUsage(user, userMessage.getTokenCount() + aiMessage.getTokenCount());
            }
        };

        // Safety net only - covers genuine async edge cases (client
        // disconnects, the emitter's own 180s timeout firing later).
        // These callbacks are NOT guaranteed to run before the client sees
        // the stream close, so they must not be the primary save path.
        customEmitter.onTimeout(persistAiReply::run);
        customEmitter.onError(throwable -> persistAiReply.run());

        // streamReply() runs synchronously in this codebase (it blocks
        // this thread, writes every chunk, then calls emitter.complete()
        // before returning) - so by the time control reaches the next
        // line, the full reply has already been generated and streamed.
        // Persist it here, synchronously, BEFORE returning the emitter -
        // this is what actually guarantees the save lands before the
        // client's own post-stream re-fetch runs.
        aiService.streamReply(history, conversation.getModel(), customEmitter);
        persistAiReply.run();

        return customEmitter;
    }

    public ConversationDto mapToDto(Conversation conversation, MessageDto lastMessage) {
        return ConversationDto.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .model(conversation.getModel())
                .isArchived(conversation.getIsArchived())
                .createdAt(conversation.getCreatedAt())
                .lastMessage(lastMessage)
                .build();
    }

    private int estimateTokens(String text) {
        if (text == null || text.isBlank()) return 0;
        return (int) Math.ceil(text.length() / 4.0);
    }
}