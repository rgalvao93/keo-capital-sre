package com.keocapital.blockchain.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockchainService {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final MeterRegistry meterRegistry;

  private Counter transactionSuccessCounter;
  private Counter transactionFailureCounter;

  @PostConstruct
  public void initMetrics() {
    transactionSuccessCounter =
        Counter.builder("keo.blockchain.transaction.success")
            .description("Blockchain transaction success count")
            .tag("service", "blockchain-mock")
            .register(meterRegistry);
    transactionFailureCounter =
        Counter.builder("keo.blockchain.transaction.failure")
            .description("Blockchain transaction failure count")
            .tag("service", "blockchain-mock")
            .register(meterRegistry);
  }

  public Map<String, Object> executeTransaction(Map<String, Object> request) {
    String txType = (String) request.getOrDefault("type", "PAYMENT");
    Double amount = ((Number) request.getOrDefault("amount", 100.0)).doubleValue();
    String from = (String) request.getOrDefault("from", "buyer");
    String to = (String) request.getOrDefault("to", "supplier");

    long latency = ThreadLocalRandom.current().nextLong(50, 201);
    try {
      Thread.sleep(latency);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean success = Math.random() < 0.998;
    String txHash = "0x" + UUID.randomUUID().toString().replace("-", "");

    Map<String, Object> result =
        Map.of(
            "txHash", txHash,
            "type", txType,
            "from", from,
            "to", to,
            "amount", amount,
            "status", success ? "CONFIRMED" : "FAILED",
            "latencyMs", latency,
            "blockNumber", ThreadLocalRandom.current().nextLong(1000000, 2000000),
            "gasUsed", ThreadLocalRandom.current().nextLong(21000, 100000),
            "timestamp", System.currentTimeMillis());

    kafkaTemplate.send("blockchain-events", result);

    if (success) transactionSuccessCounter.increment();
    else transactionFailureCounter.increment();

    log.info("Blockchain tx: {} - {} - {}ms", txHash, result.get("status"), latency);

    return result;
  }

  public Map<String, Object> getTransactionStatus(String txHash) {
    long latency = ThreadLocalRandom.current().nextLong(10, 50);
    try {
      Thread.sleep(latency);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    return Map.of(
        "txHash",
        txHash,
        "status",
        "CONFIRMED",
        "confirmations",
        ThreadLocalRandom.current().nextInt(1, 12),
        "latencyMs",
        latency);
  }
}
