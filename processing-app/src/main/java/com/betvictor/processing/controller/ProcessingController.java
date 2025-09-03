package com.betvictor.processing.controller;

import com.betvictor.processing.kafka.KafkaProducerService;
import com.betvictor.shared.model.ProcessingResult;
import com.betvictor.processing.service.HipsumService;
import com.betvictor.processing.service.TextProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/betvictor")
public class ProcessingController {
    private final HipsumService hipsumService;
    private final TextProcessingService textProcessingService;
    private final KafkaProducerService kafkaProducerService;

    public ProcessingController(HipsumService hipsumService,
                                TextProcessingService textProcessingService,
                                KafkaProducerService kafkaProducerService) {
        this.hipsumService = hipsumService;
        this.textProcessingService = textProcessingService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping("/text")
    public Mono<ResponseEntity<ProcessingResult>> processText(@RequestParam("p") int paragraphCount) {
        if (paragraphCount <= 0) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return hipsumService.fetchParagraphs(paragraphCount)
                .collectList()
                .map(paragraphs -> {
                    ProcessingResult result = textProcessingService.processText(paragraphs);
                    kafkaProducerService.sendProcessingResult(result);
                    return ResponseEntity.ok(result);
                })
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }
}