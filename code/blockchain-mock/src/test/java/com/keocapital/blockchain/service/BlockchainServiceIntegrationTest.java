package com.keocapital.blockchain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class BlockchainServiceIntegrationTest {

  @Container
  static final KafkaContainer kafkaContainer =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

  private static KafkaTemplate<String, Object> kafkaTemplate;

  @BeforeAll
  static void startKafka() {
    Map<String, Object> producerProps = new HashMap<>();
    producerProps.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));
  }

  @AfterAll
  static void stopKafka() {
    if (kafkaTemplate != null) {
      kafkaTemplate.destroy();
    }
  }

  @Test
  void executeTransactionPublishesEventToKafka() {
    BlockchainService service =
        new BlockchainService(
            kafkaTemplate, new io.micrometer.core.instrument.simple.SimpleMeterRegistry());
    service.initMetrics();

    service.executeTransaction(Map.of("amount", 100.0, "type", "PAYMENT"));

    Map<String, Object> consumerProps = new HashMap<>();
    consumerProps.put(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "blockchain-it");
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
      consumer.subscribe(Collections.singletonList("blockchain-events"));
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(15));
      assertThat(records.count()).isGreaterThan(0);
    }
  }
}
