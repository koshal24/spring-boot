package com.lms.repository;

import com.lms.model.Refund;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends MongoRepository<Refund, String> {
}
