package com.aichat.app.provider;

import com.aichat.app.entity.Message;
import util.ChatSseEmitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroqAiProvider implements AiProvider {

    private final GeminiAiProvider geminiAiProvider;

    public GroqAiProvider(GeminiAiProvider geminiAiProvider) {
        this.geminiAiProvider = geminiAiProvider;
    }

    @Override
    public String getProviderName() {
        return "Groq";
    }

    @Override
    public boolean supportsModel(String model) {
        return model != null && (model.startsWith("groq") || model.contains("llama") || model.contains("mixtral"));
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