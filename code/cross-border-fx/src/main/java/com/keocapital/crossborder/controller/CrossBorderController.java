package com.keocapital.crossborder.controller;

import com.keocapital.common.model.Transaction;
import com.keocapital.crossborder.service.CrossBorderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CrossBorderController {

    private final CrossBorderService crossBorderService;

    @PostMapping("/fx/convert")
    public ResponseEntity<Map<String, Object>> convertCurrency(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = crossBorderService.convertCurrency(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/fx/rate")
    public ResponseEntity<Map<String, Object>> getRate(@RequestParam String from, @RequestParam String to) {
        Map<String, Object> result = crossBorderService.getRate(from, to);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/crossborder/transaction")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Map<String, Object> request) {
        Transaction tx = crossBorderService.createTransaction(request);
        return ResponseEntity.ok(tx);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "cross-border-fx"));
    }
}