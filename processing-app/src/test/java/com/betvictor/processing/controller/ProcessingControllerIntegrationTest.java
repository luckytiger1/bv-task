package com.betvictor.processing.controller;

import com.betvictor.processing.kafka.KafkaProducerService;
import com.betvictor.shared.model.ProcessingResult;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProcessingControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.hipsum.base-url", () -> "http://localhost:" + mockWebServer.getPort() + "/");
        registry.add("app.kafka.topic", () -> "test.words.processed");
    }

    @Test
    void processText_shouldIntegrateAllComponents() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("[\"First hipsum paragraph with multiple words\"]")
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse()
                .setBody("[\"Second paragraph containing more test words\"]")
                .addHeader("Content-Type", "application/json"));

        webTestClient.get()
                .uri("/betvictor/text?p=2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.freq_word").exists()
                .jsonPath("$.avg_paragraph_size").exists()
                .jsonPath("$.avg_paragraph_processing_time").exists()
                .jsonPath("$.total_processing_time").exists()
                .jsonPath("$.timestamp").exists();

        verify(kafkaProducerService).sendProcessingResult(any(ProcessingResult.class));
    }

    @Test
    void processText_shouldHandleExternalApiError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        webTestClient.get()
                .uri("/betvictor/text?p=1")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}