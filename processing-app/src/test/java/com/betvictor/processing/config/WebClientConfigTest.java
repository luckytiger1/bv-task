package com.betvictor.processing.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebClientConfigTest {

    @Test
    void webClient_shouldCreateWebClientWithBaseUrl() {
        WebClientConfig config = new WebClientConfig();

        WebClient webClient = config.webClient("https://hipsum.co/api/");

        assertNotNull(webClient);
    }
}