package com.betvictor.repository.kafka;

import com.betvictor.repository.model.ProcessingResultEntity;
import com.betvictor.shared.model.ProcessingResult;
import com.betvictor.repository.service.ProcessingResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final ProcessingResultService processingResultService;

    public KafkaConsumerService(ProcessingResultService processingResultService) {
        this.processingResultService = processingResultService;
    }

    @KafkaListener(topics = "${app.kafka.topic}", containerFactory = "kafkaListenerContainerFactoryWithDlt")
    public void consume(@Payload ProcessingResult message,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                        @Header(KafkaHeaders.OFFSET) long offset) {
        logger.info("Consumed message from topic: {}, partition: {}, offset: {}",
                topic, partition, offset);
        logger.debug("Message content: {}", message);

        ProcessingResultEntity entity = ProcessingResultEntity.from(message);
        processingResultService.save(entity);

        logger.info("Successfully processed message from topic: {}, partition: {}, offset: {}",
                topic, partition, offset);
    }
}