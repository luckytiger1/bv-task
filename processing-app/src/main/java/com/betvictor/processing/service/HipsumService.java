package com.betvictor.processing.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class HipsumService {
    private final WebClient webClient;

    public HipsumService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<String> fetchParagraphs(int count) {
        return Flux.range(0, count)
                .flatMap(i -> webClient.get()
                        .uri("/?type=hipster-centric&paragraphs=1")
                        .retrieve()
                        .bodyToMono(String[].class)
                        .map(paragraphs -> paragraphs.length > 0 ? paragraphs[0] : "")
                );
    }
}