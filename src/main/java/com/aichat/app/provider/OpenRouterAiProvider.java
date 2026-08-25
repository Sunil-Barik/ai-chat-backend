package com.aichat.app.provider;

import com.aichat.app.entity.Message;
import util.ChatSseEmitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenRouterAiProvider implements AiProvider {

    private final GeminiAiProvider geminiAiProvider;

    public OpenRouterAiProvider(GeminiAiProvider geminiAiProvider) {
        this.geminiAiProvider = geminiAiProvider;
    }

    @Override
    public String getProviderName() {
        return "OpenRouter";
    }

    @Override
    public boolean supportsModel(String model) {
        return model != null && (model.startsWith("openrouter") || model.contains("gpt") || model.contains("claude"));
    }

    @Override
    public String generateResponse(List<Message> messageHistory, String model) {
        return geminiAiProvider.generateResponse(messageHistory, model);
    }

    @Override
    public void streamResponse(List<Message> messageHistory, String model, ChatSseEmitter emitter) {
        geminiAiProvider.streamResponse(messageHistory, model, emitter);
    }
}