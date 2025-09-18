package com.lms.controller;

import com.lms.model.Purchase;
import com.lms.service.PurchaseService;
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
    public List<Purchase> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    @GetMapping("/{id}")
    public Optional<Purchase> getPurchaseById(@PathVariable String id) {
        return purchaseService.getPurchaseById(id);
    }

    @PostMapping
    public Purchase createPurchase(@RequestBody Purchase purchase) {
        return purchaseService.savePurchase(purchase);
    }

    @DeleteMapping("/{id}")
    public void deletePurchase(@PathVariable String id) {
        purchaseService.deletePurchase(id);
    }
}
