package com.lms.service;

import com.lms.model.Refund;
import com.lms.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RefundService {
    @Autowired
    private RefundRepository refundRepository;
    @Autowired
    private com.lms.repository.PurchaseRepository purchaseRepository;
    @Autowired
    private com.lms.repository.UserRepository userRepository;

    public List<Refund> getAllRefunds() {
        return refundRepository.findAll();
    }

    public Optional<Refund> getRefundById(String id) {
        return refundRepository.findById(id);
    }

    public Refund saveRefund(Refund refund) {
        return refundRepository.save(refund);
    }

    public Refund saveRefundByIds(String purchaseId, String userId, double amount, java.util.Date refundDate, String reason) {
        com.lms.model.Purchase purchase = null;
        com.lms.model.User user = null;
        if (purchaseId != null) {
            purchase = purchaseRepository.findById(purchaseId).orElse(null);
            if (purchase == null) throw new IllegalArgumentException("Purchase not found: " + purchaseId);
        }
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
            if (user == null) throw new IllegalArgumentException("User not found: " + userId);
        }
        Refund r = new Refund();
        r.setPurchase(purchase);
        r.setUser(user);
        r.setAmount(amount);
        r.setRefundDate(refundDate != null ? refundDate : new java.util.Date());
        r.setReason(reason);
        return refundRepository.save(r);
    }

    public void deleteRefund(String id) {
        refundRepository.deleteById(id);
    }
}
