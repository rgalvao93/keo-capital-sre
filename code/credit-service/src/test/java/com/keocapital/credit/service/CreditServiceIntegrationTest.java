package com.keocapital.credit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.keocapital.common.model.CreditApplication;
import com.keocapital.common.model.KycVerification;
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
class CreditServiceIntegrationTest {

  @Container static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

  private static MongoClient mongoClient;
  private static MongoTemplate mongoTemplate;

  @SuppressWarnings("unchecked")
  private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);

  private CreditService creditService;

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
    creditService = new CreditService(mongoTemplate, kafkaTemplate, new SimpleMeterRegistry());
    creditService.initMetrics();
  }

  @Test
  void evaluateCreditPersistsApplication() {
    CreditApplication result = creditService.evaluateCredit(Map.of("requestedAmount", 5000.0));

    CreditApplication stored = mongoTemplate.findById(result.getId(), CreditApplication.class);

    assertThat(stored).isNotNull();
    assertThat(stored.getRequestedAmount()).isEqualTo(5000.0);
    assertThat(stored.getStatus()).isIn("APPROVED", "REJECTED");
    assertThat(stored.getKycVerificationId()).isNotNull();

    KycVerification verification =
        mongoTemplate.findById(stored.getKycVerificationId(), KycVerification.class);
    assertThat(verification).isNotNull();
    assertThat(verification.getApplicationId()).isEqualTo(stored.getId());
    assertThat(verification.getStatus()).isIn("APPROVED", "REJECTED");
    verify(kafkaTemplate).send("credit-events", result);
  }

  @Test
  void disburseWorkingCapitalPersistsTransaction() {
    Transaction result = creditService.disburseWorkingCapital(Map.of("amount", 10000.0));

    Transaction stored = mongoTemplate.findById(result.getId(), Transaction.class);

    assertThat(stored).isNotNull();
    assertThat(stored.getType()).isEqualTo("DISBURSEMENT");
    assertThat(stored.getAmount()).isEqualTo(10000.0);
  }
}
