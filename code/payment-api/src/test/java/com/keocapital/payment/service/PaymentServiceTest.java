package com.keocapital.payment.service;

import com.keocapital.common.model.Transaction;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private SimpleMeterRegistry meterRegistry;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        paymentService = new PaymentService(mongoTemplate, kafkaTemplate, meterRegistry);
        paymentService.initMetrics();
    }

    @Test
    void authorizeCardReturnsTransactionAndPersists() {
        when(mongoTemplate.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = paymentService.authorizeCard(Map.of(
                "customerId", "CUST-1",
                "region", "latam",
                "product", "keo-travel",
                "amount", 100.0));

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("AUTHORIZATION");
        assertThat(result.getCustomerId()).isEqualTo("CUST-1");
        assertThat(result.getRegion()).isEqualTo("latam");
        assertThat(result.getProduct()).isEqualTo("keo-travel");
        assertThat(result.getAmount()).isEqualTo(100.0);
        assertThat(result.getStatus()).isIn("SUCCESS", "FAILED");

        verify(mongoTemplate).save(any(Transaction.class));
        verify(kafkaTemplate).send(eq("payment-events"), any(Transaction.class));
    }

    @Test
    void processPaymentReturnsTransaction() {
        when(mongoTemplate.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = paymentService.processPayment(Map.of("amount", 500.0));

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("PAYMENT_PROCESSING");
        assertThat(result.getAmount()).isEqualTo(500.0);
        verify(mongoTemplate).save(any(Transaction.class));
    }

    @Test
    void createCardReturnsTransaction() {
        when(mongoTemplate.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = paymentService.createCard(Map.of());

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("CARD_CREATION");
        verify(mongoTemplate).save(any(Transaction.class));
    }

    @Test
    void getCardDelegatesToMongo() {
        Transaction t = Transaction.builder().id("id-1").type("AUTHORIZATION").build();
        when(mongoTemplate.findById("id-1", Transaction.class)).thenReturn(t);

        Transaction result = paymentService.getCard("id-1");

        assertThat(result).isSameAs(t);
    }
}
