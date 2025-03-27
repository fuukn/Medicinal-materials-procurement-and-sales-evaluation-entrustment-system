package io.renren.modules.sys.service.impl;

import io.renren.modules.sys.model.ChatCompletionRequest;
import io.renren.modules.sys.model.ChatCompletionResponse;
import io.renren.modules.sys.service.OpenAIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenAIServiceImpl implements OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final WebClient webClient;

    public OpenAIServiceImpl() {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    public Mono<String> getResponse(String inputData) {
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(new ChatCompletionRequest(inputData))
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .map(response -> response.getChoices().get(0).getMessage().getContent());
    }
}
