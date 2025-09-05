package com.betvictor.processing.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HipsumServiceTest {

    private MockWebServer mockWebServer;
    private HipsumService hipsumService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        hipsumService = new HipsumService(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void fetchParagraphs_shouldReturnCorrectNumberOfParagraphs() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("[\"First hipsum paragraph\"]")
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse()
                .setBody("[\"Second hipsum paragraph\"]")
                .addHeader("Content-Type", "application/json"));

        Flux<String> result = hipsumService.fetchParagraphs(2);

        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();

        assertEquals(2, mockWebServer.getRequestCount());
    }

    @Test
    void fetchParagraphs_shouldHandleEmptyResponse() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("[]")
                .addHeader("Content-Type", "application/json"));

        Flux<String> result = hipsumService.fetchParagraphs(1);

        StepVerifier.create(result)
                .expectNext("")
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        Assertions.assertNotNull(request.getPath());
        assertTrue(request.getPath().contains("/?type=hipster-centric&paragraphs=1"));
    }

    @Test
    void fetchParagraphs_shouldMakeCorrectApiCalls() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("[\"Test paragraph\"]")
                .addHeader("Content-Type", "application/json"));

        Flux<String> result = hipsumService.fetchParagraphs(1);

        StepVerifier.create(result)
                .expectNext("Test paragraph")
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        Assertions.assertNotNull(request.getPath());
        assertTrue(request.getPath().contains("type=hipster-centric"));
        assertTrue(request.getPath().contains("paragraphs=1"));
    }

    @Test
    void fetchParagraphs_shouldHandleZeroCount() {
        Flux<String> result = hipsumService.fetchParagraphs(0);

        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(0, mockWebServer.getRequestCount());
    }

    @Test
    void fetchParagraphs_shouldHandleMultipleParagraphs() {
        for (int i = 0; i < 3; i++) {
            mockWebServer.enqueue(new MockResponse()
                    .setBody("[\"Paragraph " + (i + 1) + "\"]")
                    .addHeader("Content-Type", "application/json"));
        }

        Flux<String> result = hipsumService.fetchParagraphs(3);

        StepVerifier.create(result.collectList())
                .assertNext(paragraphs -> {
                    assertEquals(3, paragraphs.size());
                    assertTrue(paragraphs.contains("Paragraph 1"));
                    assertTrue(paragraphs.contains("Paragraph 2"));
                    assertTrue(paragraphs.contains("Paragraph 3"));
                })
                .verifyComplete();
    }

    @Test
    void fetchParagraphs_shouldHandleHttpError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));

        Flux<String> result = hipsumService.fetchParagraphs(1);

        StepVerifier.create(result)
                .expectError()
                .verify();
    }

    @Test
    void fetchParagraphs_shouldHandleInvalidJson() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("invalid json")
                .addHeader("Content-Type", "application/json"));

        Flux<String> result = hipsumService.fetchParagraphs(1);

        StepVerifier.create(result)
                .expectError()
                .verify();
    }
}