package com.keocapital.common.model;

import java.time.Instant;
import java.util.List;
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
@Document(collection = "kyc_verifications")
public class KycVerification {
  @Id private String id;
  private String customerId;
  private String applicationId;
  private String provider;
  private String status;
  private String riskLevel;
  private List<KycCheck> checks;
  private String providerRequestId;
  private Long latencyMs;
  private String errorCode;
  private Instant createdAt;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class KycCheck {
    private String name;
    private String result;
    private Double score;
  }
}
