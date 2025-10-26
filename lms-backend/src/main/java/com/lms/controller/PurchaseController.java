package com.lms.controller;

import com.lms.model.Purchase;
import com.lms.service.PurchaseService;
import com.lms.dto.PurchaseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public org.springframework.http.ResponseEntity<List<Purchase>> getAllPurchases() {
        List<Purchase> purchases = purchaseService.getAllPurchases();
        return org.springframework.http.ResponseEntity.ok(purchases);
    }

    @GetMapping("/{id}")
    public org.springframework.http.ResponseEntity<Purchase> getPurchaseById(@PathVariable String id) {
        Optional<Purchase> purchase = purchaseService.getPurchaseById(id);
        return purchase.map(org.springframework.http.ResponseEntity::ok)
                .orElseGet(() -> org.springframework.http.ResponseEntity.notFound().build());
    }

    @PostMapping
    public org.springframework.http.ResponseEntity<?> createPurchase(@RequestBody PurchaseRequest req) {
        try {
            Purchase saved = purchaseService.savePurchaseByIds(req.getUserId(), req.getCourseId(), req.getPurchaseDate(), req.getAmount());
            return org.springframework.http.ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return org.springframework.http.ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<Void> deletePurchase(@PathVariable String id) {
        purchaseService.deletePurchase(id);
        return org.springframework.http.ResponseEntity.noContent().build();
    }
}
