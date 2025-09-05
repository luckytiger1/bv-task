package com.betvictor.repository.service;

import com.betvictor.repository.model.ProcessingResultEntity;
import com.betvictor.repository.repository.ProcessingResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessingResultServiceTest {

    @Mock
    private ProcessingResultRepository repository;

    private ProcessingResultService service;

    @BeforeEach
    void setUp() {
        service = new ProcessingResultService(repository);
    }

    @Test
    void save_shouldDelegateToRepository() {
        ProcessingResultEntity entity = createEntity("test", 15.5);

        service.save(entity);

        verify(repository).save(entity);
    }

    @Test
    void getLastTenResults_shouldReturnRepositoryResults() {
        List<ProcessingResultEntity> mockResults = List.of(
                createEntity("word1", 10.0),
                createEntity("word2", 20.0)
        );
        when(repository.findTop10ByOrderByCreatedAtDesc()).thenReturn(mockResults);

        List<ProcessingResultEntity> results = service.getLastTenResults();

        assertEquals(mockResults, results);
        verify(repository).findTop10ByOrderByCreatedAtDesc();
    }

    @Test
    void getLastTenResults_shouldReturnEmptyListWhenNoResults() {
        when(repository.findTop10ByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());

        List<ProcessingResultEntity> results = service.getLastTenResults();

        assertTrue(results.isEmpty());
    }

    private ProcessingResultEntity createEntity(String freqWord, double avgSize) {
        return ProcessingResultEntity.builder()
                .freqWord(freqWord)
                .avgParagraphSize(avgSize)
                .avgParagraphProcessingTime(100L)
                .totalProcessingTime(500L)
                .timestamp(LocalDateTime.now())
                .build();
    }
}