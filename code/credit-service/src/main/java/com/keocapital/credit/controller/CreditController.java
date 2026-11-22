package com.keocapital.credit.controller;

import com.keocapital.common.model.CreditApplication;
import com.keocapital.common.model.Transaction;
import com.keocapital.credit.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @PostMapping("/credit/evaluate")
    public ResponseEntity<CreditApplication> evaluateCredit(@RequestBody Map<String, Object> request) {
        CreditApplication app = creditService.evaluateCredit(request);
        return ResponseEntity.ok(app);
    }

    @PostMapping("/credit/approve")
    public ResponseEntity<CreditApplication> approveCredit(@RequestBody Map<String, Object> request) {
        CreditApplication app = creditService.approveCredit(request);
        return ResponseEntity.ok(app);
    }

    @GetMapping("/credit/{id}/status")
    public ResponseEntity<CreditApplication> getCreditStatus(@PathVariable String id) {
        CreditApplication app = creditService.getCreditStatus(id);
        return ResponseEntity.ok(app);
    }

    @PostMapping("/working-capital/request")
    public ResponseEntity<CreditApplication> requestWorkingCapital(@RequestBody Map<String, Object> request) {
        CreditApplication app = creditService.requestWorkingCapital(request);
        return ResponseEntity.ok(app);
    }

    @PostMapping("/working-capital/disburse")
    public ResponseEntity<Transaction> disburseWorkingCapital(@RequestBody Map<String, Object> request) {
        Transaction tx = creditService.disburseWorkingCapital(request);
        return ResponseEntity.ok(tx);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "credit-service"));
    }
}