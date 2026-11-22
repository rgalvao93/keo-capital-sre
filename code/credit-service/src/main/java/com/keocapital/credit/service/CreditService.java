package com.keocapital.credit.service;

import com.keocapital.common.model.CreditApplication;
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
public class CreditService {

    private final MongoTemplate mongoTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CreditApplication evaluateCredit(Map<String, Object> request) {
        String customerId = (String) request.getOrDefault("customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
        String region = (String) request.getOrDefault("region", "latam");
        String product = (String) request.getOrDefault("product", "workeo");
        Double requestedAmount = ((Number) request.getOrDefault("requestedAmount", 5000.0)).doubleValue();

        // Simulate credit scoring (85% approval rate)
        boolean approved = Math.random() < 0.85;
        double creditScore = 300 + Math.random() * 550; // 300-850

        CreditApplication app = CreditApplication.builder()
                .id(UUID.randomUUID().toString())
                .customerId(customerId)
                .product(product)
                .status(approved ? "APPROVED" : "REJECTED")
                .requestedAmount(requestedAmount)
                .approvedAmount(approved ? requestedAmount * (0.8 + Math.random() * 0.2) : 0.0)
                .creditScore(creditScore)
                .region(region)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        mongoTemplate.save(app);
        kafkaTemplate.send("credit-events", app);

        log.info("Credit evaluation: {} - {} - score: {}", app.getId(), app.getStatus(), creditScore);
        return app;
    }

    public CreditApplication approveCredit(Map<String, Object> request) {
        String customerId = (String) request.getOrDefault("customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
        String region = (String) request.getOrDefault("region", "latam");
        Double amount = ((Number) request.getOrDefault("amount", 5000.0)).doubleValue();

        CreditApplication app = CreditApplication.builder()
                .id(UUID.randomUUID().toString())
                .customerId(customerId)
                .product("workeo")
                .status("APPROVED")
                .requestedAmount(amount)
                .approvedAmount(amount)
                .creditScore(700 + Math.random() * 150)
                .region(region)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        mongoTemplate.save(app);
        kafkaTemplate.send("credit-events", app);

        log.info("Credit approved: {} - {}", app.getId(), amount);
        return app;
    }

    public CreditApplication getCreditStatus(String id) {
        return mongoTemplate.findById(id, CreditApplication.class);
    }

    public CreditApplication requestWorkingCapital(Map<String, Object> request) {
        String customerId = (String) request.getOrDefault("customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
        String region = (String) request.getOrDefault("region", "latam");
        Double amount = ((Number) request.getOrDefault("amount", 10000.0)).doubleValue();

        CreditApplication app = CreditApplication.builder()
                .id(UUID.randomUUID().toString())
                .customerId(customerId)
                .product("workeo")
                .status("PENDING")
                .requestedAmount(amount)
                .approvedAmount(0.0)
                .creditScore(0.0)
                .region(region)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        mongoTemplate.save(app);
        kafkaTemplate.send("credit-events", app);

        log.info("Working capital request: {} - {}", app.getId(), amount);
        return app;
    }

    public Transaction disburseWorkingCapital(Map<String, Object> request) {
        String customerId = (String) request.getOrDefault("customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
        String region = (String) request.getOrDefault("region", "latam");
        Double amount = ((Number) request.getOrDefault("amount", 10000.0)).doubleValue();

        // Simulate disbursement latency (P95 < 5s)
        long latency = (long) (1000 + Math.random() * 3000); // 1-4s
        boolean success = Math.random() < 0.99;

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .type("DISBURSEMENT")
                .product("workeo")
                .region(region)
                .status(success ? "SUCCESS" : "FAILED")
                .amount(amount)
                .currency("USD")
                .customerId(customerId)
                .latencyMs(latency)
                .errorMessage(success ? null : "Disbursement failed")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        mongoTemplate.save(transaction);
        kafkaTemplate.send("credit-events", transaction);

        log.info("Working capital disbursement: {} - {}ms - {}", transaction.getId(), latency, transaction.getStatus());
        return transaction;
    }
}