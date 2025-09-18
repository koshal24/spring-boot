package com.lms.repository;

import com.lms.model.Purchase;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends MongoRepository<Purchase, String> {
    // Add custom query methods if needed
}
