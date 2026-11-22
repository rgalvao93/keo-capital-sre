package com.keocapital.blockchain.controller;

import com.keocapital.blockchain.service.BlockchainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/blockchain")
@RequiredArgsConstructor
public class BlockchainController {

    private final BlockchainService blockchainService;

    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeTransaction(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = blockchainService.executeTransaction(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status/{txHash}")
    public ResponseEntity<Map<String, Object>> getTransactionStatus(@PathVariable String txHash) {
        Map<String, Object> result = blockchainService.getTransactionStatus(txHash);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "blockchain-mock"));
    }
}