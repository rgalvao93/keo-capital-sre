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
@Document(collection = "customers")
public class Customer {
  @Id private String id;
  private String name;
  private String status; // ACTIVE, SUSPENDED, CLOSED
  private String region;
  private String type; // INDIVIDUAL, ENTERPRISE
  private List<String> products;
  private Instant createdAt;
  private Instant updatedAt;
}
