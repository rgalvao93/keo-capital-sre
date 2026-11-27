package com.keocapital.credit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.keocapital.common.model.CreditApplication;
import com.keocapital.common.model.KycVerification;
import com.keocapital.common.model.Transaction;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class CreditServiceTest {

  @Mock private MongoTemplate mongoTemplate;

  @Mock private KafkaTemplate<String, Object> kafkaTemplate;

  private SimpleMeterRegistry meterRegistry;
  private CreditService creditService;

  @BeforeEach
  void setUp() {
    meterRegistry = new SimpleMeterRegistry();
    creditService = new CreditService(mongoTemplate, kafkaTemplate, meterRegistry);
    creditService.initMetrics();
  }

  @Test
  void evaluateCreditReturnsApplication() {
    when(mongoTemplate.save(any(CreditApplication.class))).thenAnswer(inv -> inv.getArgument(0));
    when(mongoTemplate.save(any(KycVerification.class)))
        .thenAnswer(
            inv -> {
              KycVerification verification = inv.getArgument(0);
              verification.setId("KYC-" + verification.getApplicationId());
              return verification;
            });
    when(mongoTemplate.save(any(Map.class), eq("kyc_verifications")))
        .thenAnswer(inv -> inv.getArgument(0));
    when(mongoTemplate.save(any(Map.class), eq("outbox_events")))
        .thenAnswer(inv -> inv.getArgument(0));
    CreditApplication result = creditService.evaluateCredit(Map.of("requestedAmount", 5000.0));
    assertThat(result).isNotNull();
    assertThat(result.getRequestedAmount()).isEqualTo(5000.0);
    assertThat(result.getKycVerificationId()).isNotNull();
    verify(mongoTemplate).save(any(CreditApplication.class));
    verify(mongoTemplate).save(any(KycVerification.class));
    verify(mongoTemplate).save(any(Map.class), eq("kyc_verifications"));
    verify(mongoTemplate).save(any(Map.class), eq("outbox_events"));
  }

  @Test
  void disburseWorkingCapitalReturnsTransaction() {
    when(mongoTemplate.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
    Transaction result = creditService.disburseWorkingCapital(Map.of("amount", 10000.0));
    assertThat(result).isNotNull();
    assertThat(result.getType()).isEqualTo("DISBURSEMENT");
    verify(mongoTemplate).save(any(Transaction.class));
  }
}
