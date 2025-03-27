package io.renren.modules.sys.service;

import reactor.core.publisher.Mono;

public interface OpenAIService {
    Mono<String> getResponse(String inputData);
}
