package com.lms.controller;

import com.lms.model.User;
import com.lms.service.UserService;
import com.lms.service.CourseService;
import com.lms.service.PurchaseService;
import com.lms.service.CourseProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private CourseProgressService courseProgressService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/report")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> getReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("users", userService.getAllUsers().size());
        report.put("courses", courseService.getAllCourses().size());
        report.put("purchases", purchaseService.getAllPurchases().size());
        report.put("courseProgress", courseProgressService.getAllCourseProgress().size());
        return ResponseEntity.ok(report);
    }

    // Add more admin endpoints as needed
}
