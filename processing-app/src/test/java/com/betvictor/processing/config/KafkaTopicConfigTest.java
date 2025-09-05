package com.betvictor.processing.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KafkaTopicConfigTest {
    @Test
    void wordsTopic_shouldCreateTopicWithConfiguredProperties() {
        KafkaTopicConfig config = new KafkaTopicConfig();
        ReflectionTestUtils.setField(config, "topicName", "words.processed");
        ReflectionTestUtils.setField(config, "partitions", 4);
        ReflectionTestUtils.setField(config, "replicas", 1);

        NewTopic topic = config.wordsTopic();

        assertEquals("words.processed", topic.name());
        assertEquals(4, topic.numPartitions());
        assertEquals(1, topic.replicationFactor());
    }
}