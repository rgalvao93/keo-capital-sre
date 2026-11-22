package com.keocapital.travel.service;

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
class TravelCardServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private SimpleMeterRegistry meterRegistry;
    private TravelCardService travelCardService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        travelCardService = new TravelCardService(mongoTemplate, kafkaTemplate, meterRegistry);
        travelCardService.initMetrics();
    }

    @Test
    void createCardReturnsTransaction() {
        when(mongoTemplate.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
        Transaction result = travelCardService.createCard(Map.of());
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("CARD_CREATION");
        verify(mongoTemplate).save(any(Transaction.class));
    }
}
