package com.keocapital.travel.service;

import com.keocapital.common.model.Card;
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
public class TravelCardService {

    private final MongoTemplate mongoTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Transaction createCard(Map<String, Object> request) {
        String customerId = (String) request.getOrDefault("customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
        String region = (String) request.getOrDefault("region", "latam");

        boolean success = Math.random() < 0.995;
        long latency = (long) (100 + Math.random() * 200); // 100-300ms

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .type("CARD_CREATION")
                .product("keo-travel")
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
        kafkaTemplate.send("travel-events", transaction);

        log.info("Travel card creation: {} - {}ms - {}", transaction.getId(), latency, transaction.getStatus());
        return transaction;
    }

    public Card getCard(String id) {
        return mongoTemplate.findById(id, Card.class);
    }

    public Transaction createTransaction(Map<String, Object> request) {
        String customerId = (String) request.getOrDefault("customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
        String region = (String) request.getOrDefault("region", "latam");
        Double amount = ((Number) request.getOrDefault("amount", 200.0)).doubleValue();

        boolean success = Math.random() < 0.99;
        long latency = (long) (50 + Math.random() * 150); // 50-200ms

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .type("TRAVEL_TRANSACTION")
                .product("keo-travel")
                .region(region)
                .status(success ? "SUCCESS" : "FAILED")
                .amount(amount)
                .currency("USD")
                .customerId(customerId)
                .latencyMs(latency)
                .errorMessage(success ? null : "Transaction failed")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        mongoTemplate.save(transaction);
        kafkaTemplate.send("travel-events", transaction);

        log.info("Travel transaction: {} - {}ms - {}", transaction.getId(), latency, transaction.getStatus());
        return transaction;
    }

    public Map<String, Object> getReport() {
        return Map.of(
            "totalCards", mongoTemplate.count(new org.springframework.data.mongodb.core.query.Query(), Card.class),
            "totalTransactions", mongoTemplate.count(new org.springframework.data.mongodb.core.query.Query(), Transaction.class),
            "service", "travel-card-service"
        );
    }
}