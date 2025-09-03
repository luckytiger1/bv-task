package com.betvictor.processing.service;

import com.betvictor.shared.model.ProcessingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextProcessingServiceTest {
    private TextProcessingService textProcessingService;

    @BeforeEach
    void setUp() {
        textProcessingService = new TextProcessingService();
    }

    @Test
    void processText_shouldReturnCorrectProcessingResult() {
        List<String> paragraphs = Arrays.asList(
                "Hello world! This is a test.",
                "Hello again. The world is beautiful.",
                "Test test test."
        );

        ProcessingResult result = textProcessingService.processText(paragraphs);

        assertNotNull(result);
        assertEquals("test", result.freqWord());
        assertEquals(26.33, result.avgParagraphSize(), 0.1);
        assertTrue(result.avgParagraphProcessingTime() >= 0);
        assertTrue(result.totalProcessingTime() >= 0);
        assertNotNull(result.timestamp());
        assertTrue(result.timestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void processText_shouldHandleEmptyList() {
        List<String> paragraphs = Collections.emptyList();

        ProcessingResult result = textProcessingService.processText(paragraphs);

        assertNotNull(result);
        assertEquals("", result.freqWord());
        assertEquals(0.0, result.avgParagraphSize());
        assertEquals(0L, result.avgParagraphProcessingTime());
        assertTrue(result.totalProcessingTime() >= 0);
        assertNotNull(result.timestamp());
    }

    @Test
    void processText_shouldHandleSingleParagraph() {
        List<String> paragraphs = Arrays.asList("Hello world hello!");

        ProcessingResult result = textProcessingService.processText(paragraphs);

        assertNotNull(result);
        assertEquals("hello", result.freqWord());
        assertEquals(18.0, result.avgParagraphSize());
        assertTrue(result.avgParagraphProcessingTime() >= 0);
        assertTrue(result.totalProcessingTime() >= 0);
        assertNotNull(result.timestamp());
    }

    @Test
    void processText_shouldHandleParagraphsWithPunctuationAndNumbers() {
        List<String> paragraphs = Arrays.asList(
                "Hello, world! 123 test.",
                "Test 456 again... hello!"
        );

        ProcessingResult result = textProcessingService.processText(paragraphs);

        assertNotNull(result);
        assertTrue(Arrays.asList("hello", "test").contains(result.freqWord()));
        assertEquals(23.5, result.avgParagraphSize());
        assertTrue(result.avgParagraphProcessingTime() >= 0);
        assertTrue(result.totalProcessingTime() >= 0);
        assertNotNull(result.timestamp());
    }

    @Test
    void processText_shouldHandleEmptyStrings() {
        List<String> paragraphs = Arrays.asList("", "   ", "Hello world");

        ProcessingResult result = textProcessingService.processText(paragraphs);

        assertNotNull(result);
        assertTrue(Arrays.asList("hello", "world").contains(result.freqWord()));
        assertEquals(4.67, result.avgParagraphSize(), 0.1);
        assertTrue(result.avgParagraphProcessingTime() >= 0);
        assertTrue(result.totalProcessingTime() >= 0);
        assertNotNull(result.timestamp());
    }

    @Test
    void processText_shouldBeCaseInsensitive() {
        List<String> paragraphs = Arrays.asList(
                "HELLO hello Hello",
                "world WORLD World"
        );

        ProcessingResult result = textProcessingService.processText(paragraphs);

        assertNotNull(result);
        assertTrue(Arrays.asList("hello", "world").contains(result.freqWord()));
        assertEquals(17.0, result.avgParagraphSize());
        assertTrue(result.avgParagraphProcessingTime() >= 0);
        assertTrue(result.totalProcessingTime() >= 0);
        assertNotNull(result.timestamp());
    }

    @Test
    void processText_shouldHandleSpecialCharacters() {
        List<String> paragraphs = Arrays.asList(
                "Hello@world#test$test",
                "Test%hello&world*"
        );

        ProcessingResult result = textProcessingService.processText(paragraphs);

        assertNotNull(result);
        assertEquals("test", result.freqWord());
        assertEquals(19.0, result.avgParagraphSize());
        assertTrue(result.avgParagraphProcessingTime() >= 0);
        assertTrue(result.totalProcessingTime() >= 0);
        assertNotNull(result.timestamp());
    }
}