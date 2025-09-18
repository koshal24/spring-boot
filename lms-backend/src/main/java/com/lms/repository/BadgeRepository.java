package com.lms.repository;

import com.lms.model.Badge;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BadgeRepository extends MongoRepository<Badge, String> {
    List<Badge> findByName(String name);
}
