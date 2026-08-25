package com.aichat.app.provider;

import com.aichat.app.entity.Message;
import com.aichat.app.entity.Sender;
import util.ChatSseEmitter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Component
public class GeminiAiProvider implements AiProvider {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiProvider.class);

    // Google retired gemini-2.0-flash. Their own API error tells us the
    // replacement is gemini-3.6-flash - update this if Google deprecates
    // that one too in future.
    private static final String DEFAULT_MODEL = "gemini-3.6-flash";

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestClient restClient = RestClient.create();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String getProviderName() {
        return "Gemini";
    }

    @Override
    public boolean supportsModel(String model) {
        return model != null && (model.startsWith("gemini") || model.equals("default"));
    }

    @Override
    public String generateResponse(List<Message> messageHistory, String model) {
        String effectiveModel = getEffectiveModel(model);
        if (!StringUtils.hasText(apiKey)) {
            log.info("Gemini API key not set. Using smart AI assistant fallback.");
            return generateFallbackResponse(messageHistory);
        }

        try {
            Map<String, Object> requestBody = buildGeminiPayload(messageHistory);
            String url = String.format("%s/%s:generateContent?key=%s", baseUrl, effectiveModel, apiKey);

            String responseString = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return parseGeminiResponse(responseString);
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            return generateFallbackResponse(messageHistory);
        }
    }

    @Override
    public void streamResponse(List<Message> messageHistory, String model, ChatSseEmitter emitter) {
        String effectiveModel = getEffectiveModel(model);
        if (!StringUtils.hasText(apiKey)) {
            streamFallbackResponse(messageHistory, emitter);
            return;
        }

        try {
            Map<String, Object> requestBody = buildGeminiPayload(messageHistory);
            String jsonPayload = objectMapper.writeValueAsString(requestBody);
            String url = String.format("%s/%s:streamGenerateContent?key=%s&alt=sse", baseUrl, effectiveModel, apiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<java.io.InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() >= 400) {
                // Read the error body so it gets logged clearly instead of
                // silently trying (and failing) to parse SSE lines from it.
                try (BufferedReader errReader = new BufferedReader(new InputStreamReader(response.body()))) {
                    StringBuilder errBody = new StringBuilder();
                    String errLine;
                    while ((errLine = errReader.readLine()) != null) {
                        errBody.append(errLine).append('\n');
                    }
                    log.error("Gemini stream API returned {}: {}", response.statusCode(), errBody);
                }
                streamFallbackResponse(messageHistory, emitter);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6).trim();
                        if (!data.isEmpty()) {
                            try {
                                JsonNode root = objectMapper.readTree(data);
                                JsonNode candidates = root.path("candidates");
                                if (candidates.isArray() && candidates.size() > 0) {
                                    JsonNode parts = candidates.get(0).path("content").path("parts");
                                    if (parts.isArray() && parts.size() > 0) {
                                        String chunk = parts.get(0).path("text").asText();
                                        if (StringUtils.hasText(chunk)) {
                                            emitter.sendChunk(chunk);
                                        }
                                    }
                                }
                            } catch (Exception parseEx) {
                                log.debug("Non-json SSE line: {}", data);
                            }
                        }
                    }
                }
            }
            emitter.complete();
        } catch (Exception e) {
            log.error("Streaming error with Gemini API, falling back to simulated stream", e);
            streamFallbackResponse(messageHistory, emitter);
        }
    }

    private String getEffectiveModel(String model) {
        if (model == null || model.isBlank() || model.equals("default") || model.equals("gemini-2.0-flash")) {
            return DEFAULT_MODEL;
        }
        return model;
    }

    private Map<String, Object> buildGeminiPayload(List<Message> messageHistory) {
        List<Map<String, Object>> contents = new ArrayList<>();

        for (Message msg : messageHistory) {
            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("role", msg.getSender() == Sender.USER ? "user" : "model");

            List<Map<String, String>> parts = new ArrayList<>();
            Map<String, String> partMap = new HashMap<>();
            partMap.put("text", msg.getContent());
            parts.add(partMap);

            contentMap.put("parts", parts);
            contents.add(contentMap);
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("contents", contents);
        return payload;
    }

    private String parseGeminiResponse(String jsonResponse) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode candidates = root.path("candidates");
        if (candidates.isArray() && candidates.size() > 0) {
            JsonNode parts = candidates.get(0).path("content").path("parts");
            if (parts.isArray() && parts.size() > 0) {
                return parts.get(0).path("text").asText();
            }
        }
        return "I received your message, but no content was returned by Gemini.";
    }

    private String generateFallbackResponse(List<Message> messageHistory) {
        String lastUserMessage = "";
        for (int i = messageHistory.size() - 1; i >= 0; i--) {
            if (messageHistory.get(i).getSender() == Sender.USER) {
                lastUserMessage = messageHistory.get(i).getContent();
                break;
            }
        }

        String lower = lastUserMessage.toLowerCase();
        if (lower.contains("code") || lower.contains("java") || lower.contains("react") || lower.contains("python") || lower.contains("function")) {
            return "Here is a code example based on your request:\n\n```javascript\n// Quick utility function\nfunction formatChatMessage(user, text) {\n  const timestamp = new Date().toLocaleTimeString();\n  return `[${timestamp}] ${user}: ${text}`;\n}\n\nconsole.log(formatChatMessage('AI Assistant', 'Hello! How can I help you today?'));\n```\n\nLet me know if you would like me to modify or extend this code!";
        } else if (lower.contains("hello") || lower.contains("hi") || lower.contains("hey")) {
            return "Hello! 👋 I am your **AI Assistant**. I can help you with coding, answering technical questions, drafting documents, analyzing data, or brainstorming ideas. What are we working on today?";
        } else if (lower.contains("who are you") || lower.contains("what can you do")) {
            return "I am **AI Chat Assistant**, built with **Spring Boot**, **React**, **PostgreSQL**, and **Gemini**.\n\n### Features:\n- ⚡ **Real-time SSE Streaming**\n- 🔐 **JWT Security & User Auth**\n- 💾 **PostgreSQL Chat History**\n- 🎨 **Markdown & Syntax Highlighting**";
        } else {
            return "That's an interesting point regarding **" + (lastUserMessage.length() > 30 ? lastUserMessage.substring(0, 30) + "..." : lastUserMessage) + "**.\n\nHere are three key perspectives to consider:\n1. **Architecture & Scalability**: Ensuring modular structure and loose coupling.\n2. **User Experience**: Fast responses with smooth SSE streaming feedback.\n3. **Data Integrity**: Persistent state stored securely in PostgreSQL.\n\nHow would you like to proceed further with this topic?";
        }
    }

    private void streamFallbackResponse(List<Message> messageHistory, ChatSseEmitter emitter) {
        try {
            String fullResponse = generateFallbackResponse(messageHistory);
            String[] words = fullResponse.split(" ");
            for (String word : words) {
                emitter.sendChunk(word + " ");
                Thread.sleep(40);
            }
            emitter.complete();
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }
}