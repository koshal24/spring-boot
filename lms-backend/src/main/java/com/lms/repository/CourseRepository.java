package com.lms.repository;

import com.lms.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByEnrolledUsers_Id(String userId);
    List<Course> findByIdIn(List<String> ids);
    List<Course> findByEducator_Id(String educatorId);
    List<Course> findTop5ByIdNotIn(List<String> ids);
    // Find courses by exact price (e.g. free courses where price == 0.0)
    List<Course> findByPrice(double price);

    // Find paid courses with pageable support so callers can request top-N sorted by id (newest)
    Page<Course> findByPaidTrue(Pageable pageable);
}
