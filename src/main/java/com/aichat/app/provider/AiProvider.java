package com.aichat.app.provider;


import com.aichat.app.entity.Message;
import util.ChatSseEmitter;

import java.util.List;

public interface AiProvider {
    String getProviderName();
    boolean supportsModel(String model);
    String generateResponse(List<Message> messageHistory, String model);
    void streamResponse(List<Message> messageHistory, String model, ChatSseEmitter emitter);
}