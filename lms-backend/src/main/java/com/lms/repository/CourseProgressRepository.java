package com.lms.repository;

import com.lms.model.CourseProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseProgressRepository extends MongoRepository<CourseProgress, String> {
    // Add custom query methods if needed
}
