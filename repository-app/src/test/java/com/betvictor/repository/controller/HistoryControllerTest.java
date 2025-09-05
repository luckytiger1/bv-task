package com.betvictor.repository.controller;

import com.betvictor.repository.model.ProcessingResultEntity;
import com.betvictor.repository.service.ProcessingResultService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HistoryController.class)
class HistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcessingResultService processingResultService;

    @Test
    void getHistory_shouldReturnResults() throws Exception {
        List<ProcessingResultEntity> mockResults = List.of(
                createEntity(1L, "hello", 15.5),
                createEntity(2L, "world", 20.0)
        );

        when(processingResultService.getLastTenResults()).thenReturn(mockResults);

        mockMvc.perform(get("/betvictor/history")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].freqWord").value("hello"))
                .andExpect(jsonPath("$[1].freqWord").value("world"));
    }

    @Test
    void getHistory_shouldReturnEmptyArrayWhenNoResults() throws Exception {
        when(processingResultService.getLastTenResults()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/betvictor/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getHistory_shouldReturn500WhenServiceThrowsException() throws Exception {
        when(processingResultService.getLastTenResults())
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/betvictor/history"))
                .andExpect(status().isInternalServerError());
    }

    private ProcessingResultEntity createEntity(Long id, String freqWord, double avgSize) {
        return ProcessingResultEntity.builder()
                .id(id)
                .freqWord(freqWord)
                .avgParagraphSize(avgSize)
                .avgParagraphProcessingTime(100L)
                .totalProcessingTime(500L)
                .timestamp(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }
}