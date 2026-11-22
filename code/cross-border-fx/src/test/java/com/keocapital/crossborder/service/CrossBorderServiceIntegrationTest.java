package com.keocapital.crossborder.service;

import com.keocapital.common.model.Transaction;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Testcontainers
class CrossBorderServiceIntegrationTest {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    private static MongoClient mongoClient;
    private static MongoTemplate mongoTemplate;

    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);

    private CrossBorderService crossBorderService;

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
        crossBorderService = new CrossBorderService(mongoTemplate, kafkaTemplate, new SimpleMeterRegistry());
        crossBorderService.initMetrics();
    }

    @Test
    void createTransactionPersists() {
        Transaction result = crossBorderService.createTransaction(Map.of("amount", 1000.0, "currency", "USD"));

        Transaction stored = mongoTemplate.findById(result.getId(), Transaction.class);

        assertThat(stored).isNotNull();
        assertThat(stored.getType()).isEqualTo("CROSS_BORDER_TRANSACTION");
        assertThat(stored.getAmount()).isEqualTo(1000.0);
        verify(kafkaTemplate).send(eq("crossborder-events"), any(Transaction.class));
    }
}
