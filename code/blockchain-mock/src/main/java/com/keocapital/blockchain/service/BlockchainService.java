package com.keocapital.blockchain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockchainService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Map<String, Object> executeTransaction(Map<String, Object> request) {
        String txType = (String) request.getOrDefault("type", "PAYMENT");
        Double amount = ((Number) request.getOrDefault("amount", 100.0)).doubleValue();
        String from = (String) request.getOrDefault("from", "buyer");
        String to = (String) request.getOrDefault("to", "supplier");

        // Simulate blockchain latency (50-200ms)
        long latency = ThreadLocalRandom.current().nextLong(50, 201);
        try {
            Thread.sleep(latency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate success rate (99.8% for blockchain)
        boolean success = Math.random() < 0.998;
        String txHash = "0x" + UUID.randomUUID().toString().replace("-", "");

        Map<String, Object> result = Map.of(
            "txHash", txHash,
            "type", txType,
            "from", from,
            "to", to,
            "amount", amount,
            "status", success ? "CONFIRMED" : "FAILED",
            "latencyMs", latency,
            "blockNumber", ThreadLocalRandom.current().nextLong(1000000, 2000000),
            "gasUsed", ThreadLocalRandom.current().nextLong(21000, 100000),
            "timestamp", System.currentTimeMillis()
        );

        kafkaTemplate.send("blockchain-events", result);
        log.info("Blockchain tx: {} - {} - {}ms", txHash, result.get("status"), latency);

        return result;
    }

    public Map<String, Object> getTransactionStatus(String txHash) {
        // Simulate transaction lookup
        long latency = ThreadLocalRandom.current().nextLong(10, 50);
        try {
            Thread.sleep(latency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return Map.of(
            "txHash", txHash,
            "status", "CONFIRMED",
            "confirmations", ThreadLocalRandom.current().nextInt(1, 12),
            "latencyMs", latency
        );
    }
}