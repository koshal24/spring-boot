package com.lms.controller;

import com.lms.model.Course;
import com.lms.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import com.lms.model.Purchase;
import com.lms.model.Subscription;
import com.lms.service.PurchaseService;
import com.lms.service.SubscriptionService;

import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/access/{courseId}/{userId}")
    public ResponseEntity<Boolean> canUserAccessCourse(@PathVariable String courseId, @PathVariable String userId) {
        Course course = courseService.getCourseById(courseId).orElse(null);
        if (course == null) return ResponseEntity.notFound().build();
        if (!course.isPaid() || course.getPrice() == 0) return ResponseEntity.ok(true);
        boolean hasPurchase = purchaseService.getPurchasesByUserId(userId).stream()
            .anyMatch(p -> p.getCourse() != null && courseId.equals(p.getCourse().getId()));
        boolean hasSubscription = subscriptionService.getSubscriptionsByUserId(userId).stream()
            .anyMatch(s -> s.isActive());
        return ResponseEntity.ok(hasPurchase || hasSubscription);
    }

    @GetMapping("/free")
    public ResponseEntity<List<Course>> getFreeCourses() {
        List<Course> freeCourses = courseService.getAllCourses().stream()
            .filter(c -> !c.isPaid() || c.getPrice() == 0).collect(toList());
//            .collection(toList());
        return ResponseEntity.ok(freeCourses);
    }

    @GetMapping("/top-free")
    public ResponseEntity<List<Course>> getTopFreeCourses(@RequestParam(required = false, defaultValue = "5") int limit,
                                                          @RequestParam(required = false, defaultValue = "popularity") String metric) {
        // metric currently supports: "popularity" (default) - sorts by enrolledUsers size
        List<Course> topFree;
        if ("popularity".equalsIgnoreCase(metric)) {
            topFree = courseService.getTopFreeCoursesByPopularity(limit);
        } else {
            // fallback to simple price-based fetch (no ordering)
            topFree = courseService.getTopFreeCourses(limit);
        }
        return ResponseEntity.ok(topFree);
    }

    @GetMapping("/top-new-paid")
    public ResponseEntity<List<Course>> getTopNewPaidCourses(@RequestParam(required = false, defaultValue = "5") int limit) {
        List<Course> topNewPaid = courseService.getTopNewPaidCourses(limit);
        return ResponseEntity.ok(topNewPaid);
    }

    @GetMapping("/top-paid-popular")
    public ResponseEntity<List<Course>> getTopPaidPopular(@RequestParam(required = false, defaultValue = "5") int limit) {
        List<Course> topPaid = courseService.getTopPaidCoursesByEnrollment(limit);
        return ResponseEntity.ok(topPaid);
    }

    @GetMapping("/paid")
    public ResponseEntity<List<Course>> getPaidCourses() {
        List<Course> paidCourses = courseService.getAllCourses().stream()
            .filter(c -> c.isPaid() && c.getPrice() > 0).collect(toList());
//            .toList();
        return ResponseEntity.ok(paidCourses);
    }

    // API for All Users, without authentication
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        Optional<Course> course = courseService.getCourseById(id);
        return course.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // API for Educator
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course saved = courseService.saveCourse(course);
        return ResponseEntity.status(201).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
