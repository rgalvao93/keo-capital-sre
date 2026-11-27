package com.keocapital.credit.service;

import com.keocapital.common.model.CreditApplication;
import com.keocapital.common.model.KycVerification;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    String appId = UUID.randomUUID().toString();
    String kycId = UUID.randomUUID().toString();
    // Persiste resultado KYC
    Map<String, Object> kyc = new java.util.HashMap<>();
    kyc.put("_id", kycId);
    kyc.put("customerId", customerId);
    kyc.put("applicationId", appId);
    kyc.put("provider", "kyc-provider-x");
    kyc.put("status", approved ? "APPROVED" : "REJECTED");
    kyc.put("riskLevel", approved ? "LOW" : "HIGH");
    kyc.put(
        "checks",
        java.util.List.of(
            Map.of(
                "name",
                "document",
                "result",
                approved ? "PASS" : "FAIL",
                "score",
                creditScore / 1000.0)));
    kyc.put("providerRequestId", UUID.randomUUID().toString());
    kyc.put("latencyMs", (long) (200 + Math.random() * 300));
    kyc.put("errorCode", approved ? null : "RISK_HIGH");
    kyc.put("createdAt", Instant.now());
    mongoTemplate.save(kyc, "kyc_verifications");

    CreditApplication app =
        CreditApplication.builder()
            .id(appId)
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
            .kycVerificationId(kycId)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    long kycLatencyMs = (long) (50 + Math.random() * 700);
    String kycStatus = app.getStatus().equals("APPROVED") ? "APPROVED" : "REJECTED";
    String riskLevel = creditScore >= 650 ? "LOW" : (creditScore >= 450 ? "MEDIUM" : "HIGH");

    KycVerification verification =
        KycVerification.builder()
            .customerId(customerId)
            .applicationId(app.getId())
            .provider("kyc-provider-x")
            .status(kycStatus)
            .riskLevel(riskLevel)
            .checks(
                java.util.List.of(
                    KycVerification.KycCheck.builder()
                        .name("document")
                        .result(kycStatus.equals("APPROVED") ? "PASS" : "FAIL")
                        .score(kycStatus.equals("APPROVED") ? 0.98 : 0.42)
                        .build()))
            .providerRequestId("KYC-" + UUID.randomUUID())
            .latencyMs(kycLatencyMs)
            .errorCode(null)
            .createdAt(Instant.now())
            .build();

    verification = mongoTemplate.save(verification);
    app.setKycVerificationId(verification.getId());
    mongoTemplate.save(app);
    // Outbox pattern: grava evento para entrega confiável via Kafka
    Map<String, Object> outbox = new java.util.HashMap<>();
    outbox.put("aggregateType", "CREDIT_APPLICATION");
    outbox.put("aggregateId", app.getId());
    outbox.put("eventType", "CREDIT_EVALUATION");
    outbox.put("topic", "credit-events");
    outbox.put("payload", app);
    outbox.put("status", "PENDING");
    outbox.put("attempts", 0);
    outbox.put("lastError", null);
    outbox.put("nextAttemptAt", Instant.now());
    outbox.put("createdAt", Instant.now());
    outbox.put("sentAt", null);
    mongoTemplate.save(outbox, "outbox_events");
    kafkaTemplate.send("credit-events", app);

    meterRegistry.counter("keo.external.kyc.status", "kyc.status", kycStatus).increment();

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
    // Outbox pattern
    Map<String, Object> outbox2 = new java.util.HashMap<>();
    outbox2.put("aggregateType", "CREDIT_APPLICATION");
    outbox2.put("aggregateId", app.getId());
    outbox2.put("eventType", "CREDIT_APPROVAL");
    outbox2.put("topic", "credit-events");
    outbox2.put("payload", app);
    outbox2.put("status", "PENDING");
    outbox2.put("attempts", 0);
    outbox2.put("lastError", null);
    outbox2.put("nextAttemptAt", Instant.now());
    outbox2.put("createdAt", Instant.now());
    outbox2.put("sentAt", null);
    mongoTemplate.save(outbox2, "outbox_events");
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

  public Map<String, Object> getCreditStatusSummary(String applicationId) {
    CreditApplication application = mongoTemplate.findById(applicationId, CreditApplication.class);
    if (application == null) {
      return null;
    }

    KycVerification verification = null;
    if (application.getKycVerificationId() != null) {
      verification =
          mongoTemplate.findById(application.getKycVerificationId(), KycVerification.class);
    } else {
      verification =
          mongoTemplate.findOne(
              Query.query(Criteria.where("applicationId").is(applicationId)),
              KycVerification.class);
    }

    return Map.of(
        "applicationId", applicationId,
        "status", application.getStatus(),
        "creditScore", application.getCreditScore(),
        "kycStatus", verification == null ? "PENDING" : verification.getStatus(),
        "kycRiskLevel", verification == null ? "UNKNOWN" : verification.getRiskLevel(),
        "kycLatencyMs", verification == null ? 0L : verification.getLatencyMs(),
        "kycProviderRequestId", verification == null ? "" : verification.getProviderRequestId());
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
    Map<String, Object> outbox3 = new java.util.HashMap<>();
    outbox3.put("aggregateType", "CREDIT_APPLICATION");
    outbox3.put("aggregateId", app.getId());
    outbox3.put("eventType", "WORKING_CAPITAL_REQUEST");
    outbox3.put("topic", "credit-events");
    outbox3.put("payload", app);
    outbox3.put("status", "PENDING");
    outbox3.put("attempts", 0);
    outbox3.put("lastError", null);
    outbox3.put("nextAttemptAt", Instant.now());
    outbox3.put("createdAt", Instant.now());
    outbox3.put("sentAt", null);
    mongoTemplate.save(outbox3, "outbox_events");
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
    Map<String, Object> outbox4 = new java.util.HashMap<>();
    outbox4.put("aggregateType", "TRANSACTION");
    outbox4.put("aggregateId", transaction.getId());
    outbox4.put("eventType", "DISBURSEMENT");
    outbox4.put("topic", "credit-events");
    outbox4.put("payload", transaction);
    outbox4.put("status", "PENDING");
    outbox4.put("attempts", 0);
    outbox4.put("lastError", null);
    outbox4.put("nextAttemptAt", Instant.now());
    outbox4.put("createdAt", Instant.now());
    outbox4.put("sentAt", null);
    mongoTemplate.save(outbox4, "outbox_events");
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
