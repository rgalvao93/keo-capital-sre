package com.keocapital.credit.service;

import com.keocapital.common.model.CreditApplication;
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
public class CreditService {

  private final MongoTemplate mongoTemplate;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final MeterRegistry meterRegistry;

  private Counter creditApprovalCounter;
  private Counter creditRejectionCounter;
  private Counter disbursementSuccessCounter;
  private Counter disbursementFailureCounter;

  @PostConstruct
  public void initMetrics() {
    creditApprovalCounter =
        Counter.builder("keo.credit.approval")
            .description("Credit approval count")
            .tag("service", "credit-service")
            .register(meterRegistry);
    creditRejectionCounter =
        Counter.builder("keo.credit.rejection")
            .description("Credit rejection count")
            .tag("service", "credit-service")
            .register(meterRegistry);
    disbursementSuccessCounter =
        Counter.builder("keo.working_capital.disbursement.success")
            .description("Working capital disbursement success count")
            .tag("service", "credit-service")
            .register(meterRegistry);
    disbursementFailureCounter =
        Counter.builder("keo.working_capital.disbursement.failure")
            .description("Working capital disbursement failure count")
            .tag("service", "credit-service")
            .register(meterRegistry);
  }

  public CreditApplication evaluateCredit(Map<String, Object> request) {
    String customerId =
        (String)
            request.getOrDefault(
                "customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
    String region = (String) request.getOrDefault("region", "latam");
    String product = (String) request.getOrDefault("product", "workeo");
    Double requestedAmount =
        AmountUtils.roundToCents((Number) request.getOrDefault("requestedAmount", 5000.0));

    boolean approved = Math.random() < 0.85;
    double creditScore = 300 + Math.random() * 550;

    CreditApplication app =
        CreditApplication.builder()
            .id(UUID.randomUUID().toString())
            .customerId(customerId)
            .product(product)
            .status(approved ? "APPROVED" : "REJECTED")
            .requestedAmount(requestedAmount)
            .approvedAmount(
                approved
                    ? AmountUtils.roundToCents(requestedAmount * (0.8 + Math.random() * 0.2))
                    : 0.0)
            .creditScore(creditScore)
            .region(region)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    mongoTemplate.save(app);
    kafkaTemplate.send("credit-events", app);

    if (approved) creditApprovalCounter.increment();
    else creditRejectionCounter.increment();

    log.info("Credit evaluation: {} - {} - score: {}", app.getId(), app.getStatus(), creditScore);
    return app;
  }

  public CreditApplication approveCredit(Map<String, Object> request) {
    String customerId =
        (String)
            request.getOrDefault(
                "customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
    String region = (String) request.getOrDefault("region", "latam");
    Double amount = AmountUtils.roundToCents((Number) request.getOrDefault("amount", 5000.0));

    CreditApplication app =
        CreditApplication.builder()
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

    // Trilha de auditoria (PCI-DSS 10.x)
    Map<String, Object> auditEvent = new java.util.HashMap<>();
    auditEvent.put("eventType", "CREDIT_LIMIT_CHANGE");
    auditEvent.put("actor", Map.of("id", "credit-service", "type", "SERVICE", "ip", "127.0.0.1"));
    auditEvent.put("resource", Map.of("type", "CREDIT_APPLICATION", "id", app.getId()));
    auditEvent.put("customerId", customerId);
    auditEvent.put("before", Map.of());
    auditEvent.put("after", Map.of("approvedAmount", amount));
    auditEvent.put("panTouched", false);
    auditEvent.put("outcome", "SUCCESS");
    auditEvent.put("correlationId", UUID.randomUUID().toString());
    auditEvent.put("service", "credit-service");
    auditEvent.put("region", region);
    auditEvent.put("createdAt", Instant.now());
    mongoTemplate.save(auditEvent, "audit_events");

    creditApprovalCounter.increment();
    log.info("Credit approved: {} - {}", app.getId(), amount);
    return app;
  }

  public CreditApplication getCreditStatus(String id) {
    return mongoTemplate.findById(id, CreditApplication.class);
  }

  public CreditApplication requestWorkingCapital(Map<String, Object> request) {
    String customerId =
        (String)
            request.getOrDefault(
                "customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
    String region = (String) request.getOrDefault("region", "latam");
    Double amount = AmountUtils.roundToCents((Number) request.getOrDefault("amount", 10000.0));

    CreditApplication app =
        CreditApplication.builder()
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
    String customerId =
        (String)
            request.getOrDefault(
                "customerId", "CUST-" + UUID.randomUUID().toString().substring(0, 8));
    String region = (String) request.getOrDefault("region", "latam");
    Double amount = AmountUtils.roundToCents((Number) request.getOrDefault("amount", 10000.0));

    long latency = (long) (1000 + Math.random() * 3000);
    boolean success = Math.random() < 0.99;

    Transaction transaction =
        Transaction.builder()
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

    if (success) disbursementSuccessCounter.increment();
    else disbursementFailureCounter.increment();

    log.info(
        "Working capital disbursement: {} - {}ms - {}",
        transaction.getId(),
        latency,
        transaction.getStatus());
    return transaction;
  }
}
