package com.lms.repository;

import com.lms.model.CourseReview;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CourseReviewRepository extends MongoRepository<CourseReview, String> {
    List<CourseReview> findByCourseId(String courseId);
    List<CourseReview> findByUserId(String userId);
}
