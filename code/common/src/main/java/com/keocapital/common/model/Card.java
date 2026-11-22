package com.keocapital.common.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cards")
public class Card {
  @Id private String id;
  private String type; // VIRTUAL, PHYSICAL
  private String product; // keo-travel, cross-border
  private String status; // ACTIVE, BLOCKED, EXPIRED
  private String customerId;
  private String cardNumber; // masked
  private Double limit;
  private String currency;
  private Instant createdAt;
  private Instant expiresAt;
}
