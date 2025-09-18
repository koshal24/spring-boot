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
    public List<Refund> getAllRefunds() {
        return refundService.getAllRefunds();
    }

    @GetMapping("/{id}")
    public Optional<Refund> getRefundById(@PathVariable String id) {
        return refundService.getRefundById(id);
    }

    @PostMapping
    public Refund createRefund(@RequestBody Refund refund) {
        return refundService.saveRefund(refund);
    }

    @DeleteMapping("/{id}")
    public void deleteRefund(@PathVariable String id) {
        refundService.deleteRefund(id);
    }
}
