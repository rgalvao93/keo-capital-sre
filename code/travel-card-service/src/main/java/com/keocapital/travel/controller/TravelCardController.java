package com.keocapital.travel.controller;

import com.keocapital.common.model.Card;
import com.keocapital.common.model.Transaction;
import com.keocapital.travel.service.TravelCardService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/travel")
@RequiredArgsConstructor
public class TravelCardController {

  private final TravelCardService travelCardService;

  @PostMapping("/card/create")
  public ResponseEntity<Transaction> createCard(@RequestBody Map<String, Object> request) {
    Transaction tx = travelCardService.createCard(request);
    return ResponseEntity.ok(tx);
  }

  @GetMapping("/card/{id}")
  public ResponseEntity<Card> getCard(@PathVariable String id) {
    Card card = travelCardService.getCard(id);
    return ResponseEntity.ok(card);
  }

  @PostMapping("/transaction")
  public ResponseEntity<Transaction> createTransaction(@RequestBody Map<String, Object> request) {
    Transaction tx = travelCardService.createTransaction(request);
    return ResponseEntity.ok(tx);
  }

  @GetMapping("/report")
  public ResponseEntity<Map<String, Object>> getReport() {
    Map<String, Object> report = travelCardService.getReport();
    return ResponseEntity.ok(report);
  }

  @GetMapping("/health")
  public ResponseEntity<Map<String, String>> health() {
    return ResponseEntity.ok(Map.of("status", "UP", "service", "travel-card-service"));
  }

  @GetMapping("/travel/health")
  public ResponseEntity<Map<String, String>> travelHealth() {
    return ResponseEntity.ok(Map.of("status", "UP", "service", "travel-card-service"));
  }
}
