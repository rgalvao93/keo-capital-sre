package com.keocapital.crossborder.service;

import com.keocapital.common.model.Transaction;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.keocapital.common.model.Transaction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrossBorderServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private SimpleMeterRegistry meterRegistry;
    private CrossBorderService crossBorderService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        crossBorderService = new CrossBorderService(mongoTemplate, kafkaTemplate, meterRegistry);
        crossBorderService.initMetrics();
    }

    @Test
    void createTransactionReturnsTransaction() {
        when(mongoTemplate.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
        Transaction result = crossBorderService.createTransaction(Map.of("amount", 100.0));
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("CROSS_BORDER_TRANSACTION");
        verify(mongoTemplate).save(any(Transaction.class));
        verify(kafkaTemplate).send(eq("crossborder-events"), any(Transaction.class));
    }
}
