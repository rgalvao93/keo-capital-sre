package com.keocapital.credit.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

  private final MongoTemplate mongoTemplate;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Scheduled(fixedDelay = 5000)
  public void publishPending() {
    Query q = new Query(Criteria.where("status").is("PENDING").and("nextAttemptAt").lte(Instant.now()));
    List<Map> pending = mongoTemplate.find(q, Map.class, "outbox_events");
    for (Map<String, Object> e : pending) {
      String id = String.valueOf(e.get("_id"));
      String topic = String.valueOf(e.get("topic"));
      Object payload = e.get("payload");
      int attempts = e.get("attempts") instanceof Number ? ((Number) e.get("attempts")).intValue() : 0;
      try {
        kafkaTemplate.send(topic, payload).get();
        mongoTemplate.updateFirst(
            new Query(Criteria.where("_id").is(id)),
            new Update().set("status", "SENT").set("sentAt", Instant.now()),
            "outbox_events");
      } catch (Exception ex) {
        int next = attempts + 1;
        long backoffSec = Math.min(60, (long) Math.pow(2, next));
        mongoTemplate.updateFirst(
            new Query(Criteria.where("_id").is(id)),
            new Update()
                .set("status", next >= 5 ? "FAILED" : "PENDING")
                .set("attempts", next)
                .set("lastError", ex.getMessage())
                .set("nextAttemptAt", Instant.now().plusSeconds(backoffSec)),
            "outbox_events");
        log.warn("Outbox publish failed for {}: {}", id, ex.getMessage());
      }
    }
  }
}