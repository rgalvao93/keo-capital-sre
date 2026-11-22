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
@Document(collection = "credit_applications")
public class CreditApplication {
  @Id private String id;
  private String customerId;
  private String product; // workeo
  private String status; // PENDING, APPROVED, REJECTED
  private Double requestedAmount;
  private Double approvedAmount;
  private Double creditScore;
  private String region;
  private Instant createdAt;
  private Instant updatedAt;
}
