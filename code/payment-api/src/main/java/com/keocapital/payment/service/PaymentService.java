package com.keocapital.payment.service;

import com.keocapital.common.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final MongoTemplate mongoTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Transaction authorizeCard(Map<String, Object> request) {
        long startTime = System.currentTimeMillis();
        String customerId = (String) request.getOrDefault("customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
        String region = (String) request.getOrDefault("region", "latam");
        String product = (String) request.getOrDefault("product", "keo-travel");
        Double amount = ((Number) request.getOrDefault("amount", 100.0)).doubleValue();

        // Simulate authorization (99.5% success rate)
        boolean success = Math.random() < 0.995;
        long latency = (long) (50 + Math.random() * 200); // 50-250ms

        Transaction transaction = Transaction.builder()
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

        log.info("Card authorization: {} - {}ms - {}", transaction.getId(), latency, transaction.getStatus());
        return transaction;
    }

    public Transaction createCard(Map<String, Object> request) {
        long startTime = System.currentTimeMillis();
        String customerId = (String) request.getOrDefault("customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
        String region = (String) request.getOrDefault("region", "latam");
        String product = (String) request.getOrDefault("product", "keo-travel");

        boolean success = Math.random() < 0.995;
        long latency = (long) (100 + Math.random() * 200); // 100-300ms

        Transaction transaction = Transaction.builder()
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
        kafkaTemplate.send("payment-events", transaction);

        log.info("Card creation: {} - {}ms - {}", transaction.getId(), latency, transaction.getStatus());
        return transaction;
    }

    public Transaction getCard(String id) {
        return mongoTemplate.findById(id, Transaction.class);
    }

    public Transaction processPayment(Map<String, Object> request) {
        String customerId = (String) request.getOrDefault("customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
        String region = (String) request.getOrDefault("region", "latam");
        String product = (String) request.getOrDefault("product", "supplier-payments");
        Double amount = ((Number) request.getOrDefault("amount", 500.0)).doubleValue();

        boolean success = Math.random() < 0.99;
        long latency = (long) (200 + Math.random() * 300); // 200-500ms

        Transaction transaction = Transaction.builder()
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

        log.info("Payment processing: {} - {}ms - {}", transaction.getId(), latency, transaction.getStatus());
        return transaction;
    }

    public Transaction getPaymentStatus(String id) {
        return mongoTemplate.findById(id, Transaction.class);
    }
}