package com.betvictor.repository.repository;

import com.betvictor.repository.model.ProcessingResultEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProcessingResultRepositoryTest {

    @Autowired
    private ProcessingResultRepository repository;

    @Test
    void findTop10ByOrderByCreatedAtDesc_shouldReturnEmptyListWhenNoData() {
        assertThat(repository.findTop10ByOrderByCreatedAtDesc()).isEmpty();
    }

    @Test
    void findTop10ByOrderByCreatedAtDesc_shouldReturnResultsOrderedByCreatedAtDesc() {
        LocalDateTime now = LocalDateTime.now();

        repository.save(createEntity("oldest", now.minusHours(1)));
        repository.save(createEntity("middle", now.minusMinutes(30)));
        repository.save(createEntity("newest", now.minusMinutes(10)));

        List<ProcessingResultEntity> results = repository.findTop10ByOrderByCreatedAtDesc();

        assertThat(results)
                .hasSize(3)
                .extracting(ProcessingResultEntity::getFreqWord)
                .containsExactly("newest", "middle", "oldest");
    }

    @Test
    void findTop10ByOrderByCreatedAtDesc_shouldReturnOnlyTop10Results() {
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 15; i++) {
            repository.save(createEntity("word" + i, now.minusMinutes(i)));
        }

        List<ProcessingResultEntity> results = repository.findTop10ByOrderByCreatedAtDesc();

        assertThat(results).hasSize(10);
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        assertThat(repository.findById(999L)).isEmpty();
    }

    private ProcessingResultEntity createEntity(String freqWord, LocalDateTime timestamp) {
        return ProcessingResultEntity.builder()
                .freqWord(freqWord)
                .avgParagraphSize(15.5)
                .avgParagraphProcessingTime(100L)
                .totalProcessingTime(500L)
                .timestamp(timestamp)
                .build();
    }
}