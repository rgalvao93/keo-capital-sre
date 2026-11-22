package com.keocapital.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String type; // AUTHORIZATION, DISBURSEMENT, CARD_CREATION, FX_CONVERSION
    private String product; // keo-travel, workeo, cross-border, supplier-payments
    private String region; // latam, canada, us, eu
    private String status; // SUCCESS, FAILED, PENDING
    private Double amount;
    private String currency;
    private String customerId;
    private String merchantId;
    private Long latencyMs;
    private String errorMessage;
    private Instant createdAt;
    private Instant updatedAt;
}