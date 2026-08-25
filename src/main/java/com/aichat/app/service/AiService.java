package com.aichat.app.service;

import com.aichat.app.entity.Message;
import com.aichat.app.provider.AiProvider;
import com.aichat.app.provider.AiProviderFactory;
import util.ChatSseEmitter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiService {

    private final AiProviderFactory aiProviderFactory;

    public AiService(AiProviderFactory aiProviderFactory) {
        this.aiProviderFactory = aiProviderFactory;
    }

    public String generateReply(List<Message> messageHistory, String model) {
        AiProvider provider = aiProviderFactory.getProvider(model);
        return provider.generateResponse(messageHistory, model);
    }

    public void streamReply(List<Message> messageHistory, String model, ChatSseEmitter emitter) {
        AiProvider provider = aiProviderFactory.getProvider(model);
        provider.streamResponse(messageHistory, model, emitter);
    }

    public String generateTitle(String firstMessage) {
        if (firstMessage == null || firstMessage.isBlank()) {
            return "New Conversation";
        }
        String clean = firstMessage.trim().replaceAll("\\s+", " ");
        if (clean.length() <= 30) {
            return clean;
        }
        return clean.substring(0, 30) + "...";
    }
}