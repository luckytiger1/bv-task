package com.betvictor.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ProcessingResult(
        @JsonProperty("freq_word")
        String freqWord,

        @JsonProperty("avg_paragraph_size")
        double avgParagraphSize,

        @JsonProperty("avg_paragraph_processing_time")
        long avgParagraphProcessingTime,

        @JsonProperty("total_processing_time")
        long totalProcessingTime,

        LocalDateTime timestamp
) {
}