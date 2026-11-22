package com.keocapital.crossborder.service;

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
public class CrossBorderService {

    private final MongoTemplate mongoTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Map<String, Object> convertCurrency(Map<String, Object> request) {
        String from = (String) request.getOrDefault("from", "USD");
        String to = (String) request.getOrDefault("to", "BRL");
        Double amount = ((Number) request.getOrDefault("amount", 1000.0)).doubleValue();

        // Simulate FX rate (with small variance)
        double baseRate = getBaseRate(from, to);
        double rate = baseRate * (0.995 + Math.random() * 0.01);
        double convertedAmount = amount * rate;

        // Simulate latency
        long latency = (long) (50 + Math.random() * 100);
        boolean success = Math.random() < 0.995;

        Map<String, Object> result = Map.of(
            "from", from,
            "to", to,
            "amount", amount,
            "rate", rate,
            "convertedAmount", convertedAmount,
            "status", success ? "SUCCESS" : "FAILED",
            "latencyMs", latency
        );

        log.info("FX conversion: {} {} -> {} {} (rate: {})", amount, from, convertedAmount, to, rate);
        return result;
    }

    public Map<String, Object> getRate(String from, String to) {
        double rate = getBaseRate(from, to);
        return Map.of(
            "from", from,
            "to", to,
            "rate", rate,
            "timestamp", System.currentTimeMillis()
        );
    }

    public Transaction createTransaction(Map<String, Object> request) {
        String customerId = (String) request.getOrDefault("customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
        String fromRegion = (String) request.getOrDefault("fromRegion", "latam");
        String toRegion = (String) request.getOrDefault("toRegion", "canada");
        Double amount = ((Number) request.getOrDefault("amount", 1000.0)).doubleValue();
        String currency = (String) request.getOrDefault("currency", "USD");

        boolean success = Math.random() < 0.99;
        long latency = (long) (200 + Math.random() * 300); // 200-500ms

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .type("CROSS_BORDER_TRANSACTION")
                .product("cross-border")
                .region(fromRegion)
                .status(success ? "SUCCESS" : "FAILED")
                .amount(amount)
                .currency(currency)
                .customerId(customerId)
                .latencyMs(latency)
                .errorMessage(success ? null : "Cross-border transaction failed")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        mongoTemplate.save(transaction);
        kafkaTemplate.send("crossborder-events", transaction);

        log.info("Cross-border transaction: {} - {}ms - {}", transaction.getId(), latency, transaction.getStatus());
        return transaction;
    }

    private double getBaseRate(String from, String to) {
        // Simplified FX rates
        if (from.equals(to)) return 1.0;
        if (from.equals("USD") && to.equals("BRL")) return 5.0;
        if (from.equals("USD") && to.equals("CAD")) return 1.35;
        if (from.equals("USD") && to.equals("EUR")) return 0.92;
        if (from.equals("BRL") && to.equals("USD")) return 0.20;
        if (from.equals("CAD") && to.equals("USD")) return 0.74;
        if (from.equals("EUR") && to.equals("USD")) return 1.09;
        return 1.0;
    }
}