package com.lms.controller;

import com.lms.model.Course;
import com.lms.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import com.lms.model.Purchase;
import com.lms.model.Subscription;
import com.lms.service.PurchaseService;
import com.lms.service.SubscriptionService;
    

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
        boolean hasPurchase = purchaseService.getAllPurchases().stream()
            .anyMatch(p -> userId.equals(p.getUserId()) && courseId.equals(p.getCourseId()));
        boolean hasSubscription = subscriptionService.getAllSubscriptions().stream()
            .anyMatch(s -> userId.equals(s.getUserId()) && s.isActive());
        return ResponseEntity.ok(hasPurchase || hasSubscription);
    }

    @GetMapping("/free")
    public ResponseEntity<List<Course>> getFreeCourses() {
        List<Course> freeCourses = courseService.getAllCourses().stream()
            .filter(c -> !c.isPaid() || c.getPrice() == 0)
            .toList();
        return ResponseEntity.ok(freeCourses);
    }

    @GetMapping("/paid")
    public ResponseEntity<List<Course>> getPaidCourses() {
        List<Course> paidCourses = courseService.getAllCourses().stream()
            .filter(c -> c.isPaid() && c.getPrice() > 0)
            .toList();
        return ResponseEntity.ok(paidCourses);
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        Optional<Course> course = courseService.getCourseById(id);
        return course.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

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
