package com.betvictor.processing.kafka;

import com.betvictor.shared.model.ProcessingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaProducerService producerService;
    private final String testTopic = "words.processed";

    @BeforeEach
    void setUp() {
        producerService = new KafkaProducerService(kafkaTemplate, testTopic);
    }

    @Test
    void sendProcessingResult_shouldSendWithFreqWordAsKey() {
        ProcessingResult result = new ProcessingResult(
                "hello",
                15.5,
                100L,
                500L,
                LocalDateTime.of(2024, 1, 1, 10, 0)
        );

        producerService.sendProcessingResult(result);

        verify(kafkaTemplate).send(testTopic, "hello", result);
    }

    @Test
    void sendProcessingResult_shouldHandleEmptyFreqWord() {
        ProcessingResult result = new ProcessingResult("", 0.0, 0L, 100L, LocalDateTime.now());

        producerService.sendProcessingResult(result);

        verify(kafkaTemplate).send(testTopic, "", result);
    }
}