package com.keocapital.payment.service;

import com.keocapital.common.model.Card;
import com.keocapital.common.model.Transaction;
import com.keocapital.common.util.AmountUtils;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

  private final MongoTemplate mongoTemplate;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final MeterRegistry meterRegistry;

  private Counter authSuccessCounter;
  private Counter authFailureCounter;
  private Counter cardCreationSuccessCounter;
  private Counter cardCreationFailureCounter;
  private Counter paymentSuccessCounter;
  private Counter paymentFailureCounter;

  @PostConstruct
  public void initMetrics() {
    authSuccessCounter =
        Counter.builder("keo.payments.authorization.success")
            .description("Card authorization success count")
            .tag("service", "payment-api")
            .register(meterRegistry);
    authFailureCounter =
        Counter.builder("keo.payments.authorization.failure")
            .description("Card authorization failure count")
            .tag("service", "payment-api")
            .register(meterRegistry);
    cardCreationSuccessCounter =
        Counter.builder("keo.payments.card_creation.success")
            .description("Card creation success count")
            .tag("service", "payment-api")
            .register(meterRegistry);
    cardCreationFailureCounter =
        Counter.builder("keo.payments.card_creation.failure")
            .description("Card creation failure count")
            .tag("service", "payment-api")
            .register(meterRegistry);
    paymentSuccessCounter =
        Counter.builder("keo.payments.processing.success")
            .description("Payment processing success count")
            .tag("service", "payment-api")
            .register(meterRegistry);
    paymentFailureCounter =
        Counter.builder("keo.payments.processing.failure")
            .description("Payment processing failure count")
            .tag("service", "payment-api")
            .register(meterRegistry);
  }

  public Transaction authorizeCard(Map<String, Object> request) {
    long startTime = System.currentTimeMillis();
    String customerId =
        (String)
            request.getOrDefault(
                "customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
    String region = (String) request.getOrDefault("region", "latam");
    String product = (String) request.getOrDefault("product", "keo-travel");
    Double amount = AmountUtils.roundToCents((Number) request.getOrDefault("amount", 100.0));

    boolean success = Math.random() < 0.995;
    long latency = (long) (50 + Math.random() * 200);

    Transaction transaction =
        Transaction.builder()
            .id(UUID.randomUUID().toString())
            .type("AUTHORIZATION")
            .product(product)
            .region(region)
            .status(success ? "SUCCESS" : "FAILED")
            .amount(amount)
            .currency("USD")
            .customerId(customerId)
            .latencyMs(latency)
            .errorMessage(success ? null : "Authorization declined")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    mongoTemplate.save(transaction);
    kafkaTemplate.send("payment-events", transaction);

    if (success) {
      authSuccessCounter.increment();
    } else {
      authFailureCounter.increment();
    }

    log.info(
        "Card authorization: {} - {}ms - {}",
        transaction.getId(),
        latency,
        transaction.getStatus());
    return transaction;
  }

  public Transaction createCard(Map<String, Object> request) {
    long startTime = System.currentTimeMillis();
    String customerId =
        (String)
            request.getOrDefault(
                "customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
    String region = (String) request.getOrDefault("region", "latam");
    String product = (String) request.getOrDefault("product", "keo-travel");

    boolean success = Math.random() < 0.995;
    long latency = (long) (100 + Math.random() * 200);

    Transaction transaction =
        Transaction.builder()
            .id(UUID.randomUUID().toString())
            .type("CARD_CREATION")
            .product(product)
            .region(region)
            .status(success ? "SUCCESS" : "FAILED")
            .amount(0.0)
            .currency("USD")
            .customerId(customerId)
            .latencyMs(latency)
            .errorMessage(success ? null : "Card creation failed")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    mongoTemplate.save(transaction);
    if (success) {
      Card card =
          Card.builder()
              .id(UUID.randomUUID().toString())
              .type("VIRTUAL")
              .product(product)
              .status("ACTIVE")
              .customerId(customerId)
              .cardNumber("**** **** **** " + UUID.randomUUID().toString().substring(0, 4))
              .limit(10000.0)
              .currency("USD")
              .createdAt(Instant.now())
              .expiresAt(Instant.now().plusSeconds(3 * 365 * 24 * 60 * 60))
              .build();
      mongoTemplate.save(card);
    }
    kafkaTemplate.send("payment-events", transaction);

    if (success) {
      cardCreationSuccessCounter.increment();
    } else {
      cardCreationFailureCounter.increment();
    }

    log.info(
        "Card creation: {} - {}ms - {}", transaction.getId(), latency, transaction.getStatus());
    return transaction;
  }

  public Transaction getCard(String id) {
    return mongoTemplate.findById(id, Transaction.class);
  }

  public Transaction processPayment(Map<String, Object> request) {
    String customerId =
        (String)
            request.getOrDefault(
                "customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
    String region = (String) request.getOrDefault("region", "latam");
    String product = (String) request.getOrDefault("product", "supplier-payments");
    Double amount = AmountUtils.roundToCents((Number) request.getOrDefault("amount", 500.0));

    boolean success = Math.random() < 0.99;
    long latency = (long) (200 + Math.random() * 300);

    Transaction transaction =
        Transaction.builder()
            .id(UUID.randomUUID().toString())
            .type("PAYMENT_PROCESSING")
            .product(product)
            .region(region)
            .status(success ? "SUCCESS" : "FAILED")
            .amount(amount)
            .currency("USD")
            .customerId(customerId)
            .latencyMs(latency)
            .errorMessage(success ? null : "Payment processing failed")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    mongoTemplate.save(transaction);
    kafkaTemplate.send("payment-events", transaction);

    if (success) {
      paymentSuccessCounter.increment();
    } else {
      paymentFailureCounter.increment();
    }

    log.info(
        "Payment processing: {} - {}ms - {}",
        transaction.getId(),
        latency,
        transaction.getStatus());
    return transaction;
  }

  public Transaction getPaymentStatus(String id) {
    return mongoTemplate.findById(id, Transaction.class);
  }
}
