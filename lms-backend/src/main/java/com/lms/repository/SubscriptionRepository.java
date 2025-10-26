package com.lms.repository;

import com.lms.model.Subscription;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
	List<Subscription> findByUser_Id(String userId);
	List<Subscription> findByActiveTrue();
}
