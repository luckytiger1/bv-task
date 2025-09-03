package com.betvictor.processing.service;

import com.betvictor.shared.model.ProcessingResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TextProcessingService {
    public ProcessingResult processText(List<String> paragraphs) {
        long totalStartTime = System.currentTimeMillis();

        String mostFrequentWord = findMostFrequentWord(paragraphs);
        double avgParagraphSize = calculateAverageParagraphSize(paragraphs);

        long totalEndTime = System.currentTimeMillis();
        long totalProcessingTime = totalEndTime - totalStartTime;
        long avgParagraphProcessingTime = paragraphs.isEmpty() ? 0L : totalProcessingTime / paragraphs.size();

        return new ProcessingResult(
                mostFrequentWord,
                avgParagraphSize,
                avgParagraphProcessingTime,
                totalProcessingTime,
                LocalDateTime.now()
        );
    }

    private String findMostFrequentWord(List<String> paragraphs) {
        return paragraphs.stream()
                .flatMap(paragraph -> Arrays.stream(paragraph.toLowerCase()
                        .replaceAll("[^a-zA-Z\\s]", " ")
                        .split("\\s+")))
                .filter(word -> !word.trim().isEmpty())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
    }

    private double calculateAverageParagraphSize(List<String> paragraphs) {
        return paragraphs.stream()
                .mapToInt(String::length)
                .average()
                .orElse(0.0);
    }
}