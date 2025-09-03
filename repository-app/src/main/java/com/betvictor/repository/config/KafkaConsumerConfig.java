package com.betvictor.repository.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.util.backoff.BackOff;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Value("${app.kafka.dlt.topic}")
    private String dltTopic;

    @Value("${app.kafka.dlt.max-attempts}")
    private int maxAttempts;

    @Bean
    public DefaultErrorHandler errorHandler(@Autowired KafkaTemplate<String, Object> kafkaTemplate) {
        BackOff backOff = new ExponentialBackOffWithMaxRetries(maxAttempts - 1);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            // Send failed message to DLT
            try {
                kafkaTemplate.send(dltTopic, (String) record.key(), record.value());
                logger.error("Message sent to DLT topic '{}': {}, Exception: {}",
                        dltTopic, record.value(), exception.getMessage());
            } catch (Exception e) {
                logger.error("Failed to send message to DLT topic: {}", e.getMessage());
            }
        }, backOff);

        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactoryWithDlt(
            ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory,
            @Autowired KafkaTemplate<String, Object> kafkaTemplate) {
        kafkaListenerContainerFactory.setCommonErrorHandler(errorHandler(kafkaTemplate));
        return kafkaListenerContainerFactory;
    }
}