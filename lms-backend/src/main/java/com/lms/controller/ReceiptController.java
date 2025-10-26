package com.lms.controller;

import com.lms.model.Purchase;
import com.lms.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments/receipt")
public class ReceiptController {
    @Autowired
    private PurchaseRepository purchaseRepository;

    @GetMapping("/{purchaseId}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable String purchaseId) {
        Optional<Purchase> purchaseOpt = purchaseRepository.findById(purchaseId);
        if (purchaseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Purchase purchase = purchaseOpt.get();
        StringBuilder receipt = new StringBuilder();
        receipt.append("Receipt\n");
        receipt.append("Purchase ID: ").append(purchase.getId()).append("\n");
        receipt.append("User ID: ");
        if (purchase.getUser() != null) {
            receipt.append(purchase.getUser().getId());
        } else {
            receipt.append("(unknown)");
        }
        receipt.append("\n");

        receipt.append("Course ID: ");
        if (purchase.getCourse() != null) {
            receipt.append(purchase.getCourse().getId());
        } else {
            receipt.append("(unknown)");
        }
        receipt.append("\n");
        receipt.append("Amount: $").append(purchase.getAmount()).append("\n");
        receipt.append("Date: ").append(purchase.getPurchaseDate()).append("\n");
        byte[] receiptBytes = receipt.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receipt_" + purchaseId + ".txt");
        headers.setContentType(MediaType.TEXT_PLAIN);
        return ResponseEntity.ok().headers(headers).body(receiptBytes);
    }
}
