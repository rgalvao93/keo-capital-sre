package com.keocapital.credit.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

  private final MongoTemplate mongoTemplate;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final MeterRegistry meterRegistry;

  private Counter failedCounter;

  @PostConstruct
  public void initMetrics() {
    failedCounter =
        Counter.builder("keo.outbox.failed.count")
            .description("Outbox failed publish count")
            .tag("service", "credit-service")
            .register(meterRegistry);
    Gauge.builder("keo.outbox.pending.age", this, o -> {
          Query q = new Query(Criteria.where("status").is("PENDING"));
          List<Map> p = mongoTemplate.find(q, Map.class, "outbox_events");
          if (p.isEmpty()) return 0.0;
          Instant oldest = Instant.now();
          for (Map m : p) {
            Object c = m.get("createdAt");
            if (c instanceof Instant && ((Instant) c).isBefore(oldest)) oldest = (Instant) c;
          }
          return (double) (Instant.now().toEpochMilli() - oldest.toEpochMilli()) / 1000.0;
        })
        .description("Age in seconds of oldest pending outbox event")
        .tag("service", "credit-service")
        .register(meterRegistry);
  }

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
        failedCounter.increment();
        log.warn("Outbox publish failed for {}: {}", id, ex.getMessage());
      }
    }
  }
}