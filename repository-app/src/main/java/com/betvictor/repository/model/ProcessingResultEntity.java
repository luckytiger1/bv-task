package com.betvictor.repository.model;

import com.betvictor.shared.model.ProcessingResult;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "processing_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "freq_word", nullable = false)
    private String freqWord;

    @Column(name = "avg_paragraph_size", nullable = false)
    private double avgParagraphSize;

    @Column(name = "avg_paragraph_processing_time", nullable = false)
    private long avgParagraphProcessingTime;

    @Column(name = "total_processing_time", nullable = false)
    private long totalProcessingTime;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public static ProcessingResultEntity from(ProcessingResult message) {
        return ProcessingResultEntity.builder()
                .freqWord(message.freqWord())
                .avgParagraphSize(message.avgParagraphSize())
                .avgParagraphProcessingTime(message.avgParagraphProcessingTime())
                .totalProcessingTime(message.totalProcessingTime())
                .timestamp(message.timestamp())
                .build();
    }
}