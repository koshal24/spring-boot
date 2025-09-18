package com.lms.service;

import com.lms.model.Purchase;
import com.lms.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public Optional<Purchase> getPurchaseById(String id) {
        return purchaseRepository.findById(id);
    }

    public Purchase savePurchase(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    public void deletePurchase(String id) {
        purchaseRepository.deleteById(id);
    }
}
