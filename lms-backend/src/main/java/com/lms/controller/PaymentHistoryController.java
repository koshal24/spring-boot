package com.lms.controller;

import com.lms.model.Purchase;
import com.lms.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments/history")
public class PaymentHistoryController {
    @Autowired
    private PurchaseRepository purchaseRepository;

    @GetMapping("/{userId}")
    public org.springframework.http.ResponseEntity<List<Purchase>> getUserPaymentHistory(@PathVariable String userId) {
        List<Purchase> history = purchaseRepository.findAll().stream()
                .filter(p -> userId.equals(p.getUserId()))
                .collect(Collectors.toList());
        return org.springframework.http.ResponseEntity.ok(history);
    }
}
