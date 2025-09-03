package com.betvictor.repository.controller;

import com.betvictor.repository.model.ProcessingResultEntity;
import com.betvictor.repository.service.ProcessingResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/betvictor")
public class HistoryController {
    private final ProcessingResultService processingResultService;

    public HistoryController(ProcessingResultService processingResultService) {
        this.processingResultService = processingResultService;
    }

    @GetMapping("/history")
    public ResponseEntity<List<ProcessingResultEntity>> getHistory() {
        try {
            List<ProcessingResultEntity> results = processingResultService.getLastTenResults();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}