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

    public List<Refund> getAllRefunds() {
        return refundRepository.findAll();
    }

    public Optional<Refund> getRefundById(String id) {
        return refundRepository.findById(id);
    }

    public Refund saveRefund(Refund refund) {
        return refundRepository.save(refund);
    }

    public void deleteRefund(String id) {
        refundRepository.deleteById(id);
    }
}
