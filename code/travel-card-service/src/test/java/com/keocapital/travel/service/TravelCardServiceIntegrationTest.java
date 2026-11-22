package com.keocapital.travel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.keocapital.common.model.Transaction;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class TravelCardServiceIntegrationTest {

  @Container static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

  private static MongoClient mongoClient;
  private static MongoTemplate mongoTemplate;

  @SuppressWarnings("unchecked")
  private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);

  private TravelCardService travelCardService;

  @BeforeAll
  static void startMongo() {
    mongoClient = MongoClients.create(mongoDBContainer.getConnectionString());
    mongoTemplate = new MongoTemplate(mongoClient, "keo-capital-test");
  }

  @AfterAll
  static void stopMongo() {
    if (mongoClient != null) {
      mongoClient.close();
    }
  }

  @BeforeEach
  void setUp() {
    mongoTemplate.getDb().drop();
    travelCardService =
        new TravelCardService(mongoTemplate, kafkaTemplate, new SimpleMeterRegistry());
    travelCardService.initMetrics();
  }

  @Test
  void createCardPersistsTransaction() {
    Transaction result =
        travelCardService.createCard(Map.of("customerId", "CUST-TRAVEL-1", "region", "latam"));

    Transaction stored = mongoTemplate.findById(result.getId(), Transaction.class);

    assertThat(stored).isNotNull();
    assertThat(stored.getType()).isEqualTo("CARD_CREATION");
    assertThat(stored.getProduct()).isEqualTo("keo-travel");
    verify(kafkaTemplate).send(eq("travel-events"), any(Transaction.class));
  }
}
