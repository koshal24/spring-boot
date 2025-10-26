package com.lms.repository;

import com.lms.model.Purchase;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends MongoRepository<Purchase, String> {
    List<Purchase> findByUser_Id(String userId);
    List<Purchase> findByCourse_Id(String courseId);
}
