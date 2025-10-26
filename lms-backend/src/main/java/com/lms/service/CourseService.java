package com.lms.service;

import com.lms.model.Course;
import com.lms.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import java.util.List;
import java.util.Optional;
import java.time.Instant;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(String id) {
        return courseRepository.findById(id);
    }

    public Course saveCourse(Course course) {
        if (course.getCreatedAt() == null) {
            course.setCreatedAt(Instant.now());
        }
        return courseRepository.save(course);
    }

    public List<Course> getCoursesByEnrolledUserId(String userId) {
        return courseRepository.findByEnrolledUsers_Id(userId);
    }

    public List<Course> getCoursesByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return courseRepository.findByIdIn(ids);
    }

    public List<Course> getCoursesByEducatorId(String educatorId) {
        return courseRepository.findByEducator_Id(educatorId);
    }

    public List<Course> getRecommendedCoursesExcluding(List<String> excludedCourseIds, int limit) {
        if (excludedCourseIds == null) excludedCourseIds = List.of();
        if (excludedCourseIds.isEmpty()) {
            // If no exclusions, return top N courses (repository can handle empty list)
            return courseRepository.findAll().stream().limit(limit).collect(java.util.stream.Collectors.toList());
        }
        return courseRepository.findTop5ByIdNotIn(excludedCourseIds).stream().limit(limit).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Return top N free courses (price == 0.0). If limit <= 0, returns empty list.
     */
    public List<Course> getTopFreeCourses(int limit) {
        if (limit <= 0) return List.of();
        List<Course> freeCourses = courseRepository.findByPrice(0.0);
        return freeCourses.stream().limit(limit).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Return top N free courses ordered by popularity (number of enrolled users).
     * Uses a MongoDB aggregation to compute the size of the enrolledUsers array and sort by it.
     */
    public List<Course> getTopFreeCoursesByPopularity(int limit) {
        if (limit <= 0) return List.of();

        Aggregation agg = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("price").is(0.0)),
            Aggregation.project("id", "title", "description", "educator", "enrolledUsers", "price", "paid")
                .andExpression("size(enrolledUsers)").as("enrollmentCount"),
            Aggregation.sort(Sort.by(Sort.Direction.DESC, "enrollmentCount")),
            Aggregation.limit(limit)
        );

        AggregationResults<Course> results = mongoTemplate.aggregate(agg, "courses", Course.class);
        return results.getMappedResults();
    }

    /**
     * Return top N paid courses ordered by popularity (number of enrolled users).
     * Uses a MongoDB aggregation to compute the size of the enrolledUsers array and sort by it.
     */
    public List<Course> getTopPaidCoursesByEnrollment(int limit) {
        if (limit <= 0) return List.of();

        Aggregation agg = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("paid").is(true)),
            Aggregation.project("id", "title", "description", "educator", "enrolledUsers", "price", "paid", "createdAt")
                .andExpression("size(enrolledUsers)").as("enrollmentCount"),
            Aggregation.sort(Sort.by(Sort.Direction.DESC, "enrollmentCount")),
            Aggregation.limit(limit)
        );

        AggregationResults<Course> results = mongoTemplate.aggregate(agg, "courses", Course.class);
        return results.getMappedResults();
    }

    /**
     * Return top N newly released paid courses (sorted by id desc which approximates newest first).
     * Uses pageable repository query to let MongoDB apply limit and sort.
     */
    public List<Course> getTopNewPaidCourses(int limit) {
        if (limit <= 0) return List.of();
        Pageable pageable = PageRequest.of(0, limit, Direction.DESC, "createdAt");
        Page<Course> page = courseRepository.findByPaidTrue(pageable);
        return page.getContent();
    }

    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }
}
