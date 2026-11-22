package com.keocapital.blockchain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class BlockchainServiceTest {

  @Mock private KafkaTemplate<String, Object> kafkaTemplate;

  private SimpleMeterRegistry meterRegistry;
  private BlockchainService blockchainService;

  @BeforeEach
  void setUp() {
    meterRegistry = new SimpleMeterRegistry();
    blockchainService = new BlockchainService(kafkaTemplate, meterRegistry);
    blockchainService.initMetrics();
  }

  @Test
  void executeTransactionReturnsMap() {
    Map<String, Object> result = blockchainService.executeTransaction(Map.of("amount", 100.0));
    assertThat(result).isNotNull();
    verify(kafkaTemplate).send(any(), any());
  }
}
