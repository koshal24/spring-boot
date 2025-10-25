package com.lms.controller;

import com.lms.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private StripeService stripeService;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody Map<String, Object> request) {
        long amount = Long.parseLong(request.get("amount").toString());
        String currency = request.getOrDefault("currency", "usd").toString();
        String userId = request.containsKey("userId") && request.get("userId") != null ? request.get("userId").toString() : null;
        String courseId = request.containsKey("courseId") && request.get("courseId") != null ? request.get("courseId").toString() : null;

        Map<String, Object> response = new HashMap<>();
        try {
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(amount, currency, userId, courseId);
            response.put("clientSecret", paymentIntent.getClientSecret());
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
