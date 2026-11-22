package com.keocapital.payment.controller;

import com.keocapital.common.model.Transaction;
import com.keocapital.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/card/authorize")
    public ResponseEntity<Transaction> authorizeCard(@RequestBody Map<String, Object> request) {
        Transaction transaction = paymentService.authorizeCard(request);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/card/create")
    public ResponseEntity<Transaction> createCard(@RequestBody Map<String, Object> request) {
        Transaction transaction = paymentService.createCard(request);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/card/{id}")
    public ResponseEntity<Transaction> getCard(@PathVariable String id) {
        Transaction transaction = paymentService.getCard(id);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/payment/process")
    public ResponseEntity<Transaction> processPayment(@RequestBody Map<String, Object> request) {
        Transaction transaction = paymentService.processPayment(request);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/payment/{id}/status")
    public ResponseEntity<Transaction> getPaymentStatus(@PathVariable String id) {
        Transaction transaction = paymentService.getPaymentStatus(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "payment-api"));
    }
}