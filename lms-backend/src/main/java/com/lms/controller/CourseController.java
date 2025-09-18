import com.lms.model.Purchase;
import com.lms.model.Subscription;
import com.lms.service.PurchaseService;
import com.lms.service.SubscriptionService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private SubscriptionService subscriptionService;
    @GetMapping("/access/{courseId}/{userId}")
    public boolean canUserAccessCourse(@PathVariable String courseId, @PathVariable String userId) {
        Course course = courseService.getCourseById(courseId).orElse(null);
        if (course == null) return false;
        if (!course.isPaid() || course.getPrice() == 0) return true;
        boolean hasPurchase = purchaseService.getAllPurchases().stream()
            .anyMatch(p -> userId.equals(p.getUserId()) && courseId.equals(p.getCourseId()));
        boolean hasSubscription = subscriptionService.getAllSubscriptions().stream()
            .anyMatch(s -> userId.equals(s.getUserId()) && s.isActive());
        return hasPurchase || hasSubscription;
    }
    @GetMapping("/free")
    public List<Course> getFreeCourses() {
        return courseService.getAllCourses().stream()
            .filter(c -> !c.isPaid() || c.getPrice() == 0)
            .toList();
    }

    @GetMapping("/paid")
    public List<Course> getPaidCourses() {
        return courseService.getAllCourses().stream()
            .filter(c -> c.isPaid() && c.getPrice() > 0)
            .toList();
    }
package com.lms.controller;

import com.lms.model.Course;
import com.lms.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public Optional<Course> getCourseById(@PathVariable String id) {
        return courseService.getCourseById(id);
    }

    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseService.saveCourse(course);
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
    }
}
