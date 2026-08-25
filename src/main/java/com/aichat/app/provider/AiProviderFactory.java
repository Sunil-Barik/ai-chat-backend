package com.aichat.app.provider;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiProviderFactory {

    private final List<AiProvider> providers;
    private final GeminiAiProvider defaultProvider;

    public AiProviderFactory(List<AiProvider> providers, GeminiAiProvider defaultProvider) {
        this.providers = providers;
        this.defaultProvider = defaultProvider;
    }

    public AiProvider getProvider(String model) {
        if (model == null || model.isBlank()) {
            return defaultProvider;
        }

        return providers.stream()
                .filter(p -> p.supportsModel(model))
                .findFirst()
                .orElse(defaultProvider);
    }
}
