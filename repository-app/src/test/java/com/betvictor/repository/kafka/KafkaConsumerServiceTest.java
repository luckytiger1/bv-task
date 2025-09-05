package com.betvictor.repository.kafka;

import com.betvictor.repository.model.ProcessingResultEntity;
import com.betvictor.repository.service.ProcessingResultService;
import com.betvictor.shared.model.ProcessingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private ProcessingResultService processingResultService;

    private KafkaConsumerService consumerService;

    @BeforeEach
    void setUp() {
        consumerService = new KafkaConsumerService(processingResultService);
    }

    @Test
    void consume_shouldMapAllFieldsCorrectly() {
        LocalDateTime timestamp = LocalDateTime.of(2025, 9, 1, 10, 0);
        ProcessingResult message = new ProcessingResult(
                "hello",
                15.5,
                100L,
                500L,
                timestamp
        );

        consumerService.consume(message, "words.processed", 0, 123L);

        ArgumentCaptor<ProcessingResultEntity> captor = ArgumentCaptor.forClass(ProcessingResultEntity.class);
        verify(processingResultService).save(captor.capture());

        ProcessingResultEntity entity = captor.getValue();
        assertEquals("hello", entity.getFreqWord());
        assertEquals(15.5, entity.getAvgParagraphSize());
        assertEquals(100L, entity.getAvgParagraphProcessingTime());
        assertEquals(500L, entity.getTotalProcessingTime());
        assertEquals(timestamp, entity.getTimestamp());
    }

    @Test
    void consume_shouldHandleEmptyValues() {
        ProcessingResult message = new ProcessingResult("", 0.0, 0L, 0L, LocalDateTime.now());

        consumerService.consume(message, "words.processed", 0, 123L);

        ArgumentCaptor<ProcessingResultEntity> captor = ArgumentCaptor.forClass(ProcessingResultEntity.class);
        verify(processingResultService).save(captor.capture());

        ProcessingResultEntity entity = captor.getValue();
        assertEquals("", entity.getFreqWord());
        assertEquals(0.0, entity.getAvgParagraphSize());
        assertEquals(0L, entity.getAvgParagraphProcessingTime());
        assertEquals(0L, entity.getTotalProcessingTime());
    }
}