package com.lms.controller;

import com.lms.model.Purchase;
import com.lms.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/payments/analytics")
public class PaymentAnalyticsController {
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public org.springframework.http.ResponseEntity<Map<String, Object>> getPaymentAnalytics() {
        double totalRevenue = purchaseService.getTotalRevenue();
        int totalPayments = purchaseService.getAllPurchases().size();
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalRevenue", totalRevenue);
        analytics.put("totalPayments", totalPayments);
        return org.springframework.http.ResponseEntity.ok(analytics);
    }
}
