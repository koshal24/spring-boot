package com.lms.controller;


import com.lms.model.Refund;
import com.lms.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {
    @Autowired
    private RefundService refundService;

    @GetMapping
    public org.springframework.http.ResponseEntity<List<Refund>> getAllRefunds() {
        List<Refund> refunds = refundService.getAllRefunds();
        return org.springframework.http.ResponseEntity.ok(refunds);
    }

    @GetMapping("/{id}")
    public org.springframework.http.ResponseEntity<Refund> getRefundById(@PathVariable String id) {
        Optional<Refund> refund = refundService.getRefundById(id);
        return refund.map(org.springframework.http.ResponseEntity::ok)
                .orElseGet(() -> org.springframework.http.ResponseEntity.notFound().build());
    }

    @PostMapping
    public org.springframework.http.ResponseEntity<Refund> createRefund(@RequestBody Refund refund) {
        Refund saved = refundService.saveRefund(refund);
        return org.springframework.http.ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<Void> deleteRefund(@PathVariable String id) {
        refundService.deleteRefund(id);
        return org.springframework.http.ResponseEntity.noContent().build();
    }
}
