package com.keocapital.payment.service;

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
class PaymentServiceIntegrationTest {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    private static MongoClient mongoClient;
    private static MongoTemplate mongoTemplate;

    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);

    private PaymentService paymentService;

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
        paymentService = new PaymentService(mongoTemplate, kafkaTemplate, new SimpleMeterRegistry());
        paymentService.initMetrics();
    }

    @Test
    void authorizeCardPersistsTransactionInMongoAndPublishesEvent() {
        Transaction result = paymentService.authorizeCard(Map.of(
                "customerId", "CUST-INT-1",
                "region", "latam",
                "product", "keo-travel",
                "amount", 250.0));

        Transaction stored = mongoTemplate.findById(result.getId(), Transaction.class);

        assertThat(stored).isNotNull();
        assertThat(stored.getCustomerId()).isEqualTo("CUST-INT-1");
        assertThat(stored.getType()).isEqualTo("AUTHORIZATION");
        assertThat(stored.getAmount()).isEqualTo(250.0);
        assertThat(stored.getStatus()).isIn("SUCCESS", "FAILED");
        verify(kafkaTemplate).send(eq("payment-events"), any(Transaction.class));
    }

    @Test
    void processPaymentPersistsTransactionInMongo() {
        Transaction result = paymentService.processPayment(Map.of("amount", 750.0));

        Transaction stored = mongoTemplate.findById(result.getId(), Transaction.class);

        assertThat(stored).isNotNull();
        assertThat(stored.getType()).isEqualTo("PAYMENT_PROCESSING");
        assertThat(stored.getAmount()).isEqualTo(750.0);
    }
}
