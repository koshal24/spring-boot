package com.lms.controller;

import com.lms.model.CourseReview;
import com.lms.service.CourseReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/course-reviews")
public class CourseReviewController {
    @Autowired
    private CourseReviewService courseReviewService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseReview>> getReviewsByCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(courseReviewService.getReviewsByCourseId(courseId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CourseReview>> getReviewsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(courseReviewService.getReviewsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseReview> getReviewById(@PathVariable String id) {
        Optional<CourseReview> review = courseReviewService.getReviewById(id);
        return review.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CourseReview> createReview(@RequestBody CourseReview review) {
        CourseReview saved = courseReviewService.saveReview(review);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable String id) {
        courseReviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
