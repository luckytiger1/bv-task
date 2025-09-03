package com.betvictor.processing.kafka;

import com.betvictor.shared.model.ProcessingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate,
                                @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendProcessingResult(ProcessingResult result) {
        kafkaTemplate.send(topic, result.freqWord(), result);
    }
}