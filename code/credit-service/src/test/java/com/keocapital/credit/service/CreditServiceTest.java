package com.keocapital.credit.service;

import com.keocapital.common.model.CreditApplication;
import com.keocapital.common.model.Transaction;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

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
        CreditApplication result = creditService.evaluateCredit(Map.of("requestedAmount", 5000.0));
        assertThat(result).isNotNull();
        assertThat(result.getRequestedAmount()).isEqualTo(5000.0);
        verify(mongoTemplate).save(any(CreditApplication.class));
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
