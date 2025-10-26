package com.lms.repository;

import com.lms.model.CourseProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseProgressRepository extends MongoRepository<CourseProgress, String> {
    List<CourseProgress> findByCourse_Id(String courseId);
}
