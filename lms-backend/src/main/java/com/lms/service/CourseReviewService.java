package com.lms.service;

import com.lms.model.CourseReview;
import com.lms.repository.CourseReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CourseReviewService {
    @Autowired
    private CourseReviewRepository courseReviewRepository;

    public List<CourseReview> getReviewsByCourseId(String courseId) {
        return courseReviewRepository.findByCourseId(courseId);
    }

    public List<CourseReview> getReviewsByUserId(String userId) {
        return courseReviewRepository.findByUserId(userId);
    }

    public Optional<CourseReview> getReviewById(String id) {
        return courseReviewRepository.findById(id);
    }

    public CourseReview saveReview(CourseReview review) {
        return courseReviewRepository.save(review);
    }

    public void deleteReview(String id) {
        courseReviewRepository.deleteById(id);
    }
}
