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
import com.lms.dto.RoleChangeRequest;
import com.lms.repository.UserRepository;
import jakarta.validation.Valid;

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
    private UserRepository userRepository;
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/users/{id}/role")
    public ResponseEntity<Map<String, Object>> changeUserRole(@PathVariable String id, @Valid @RequestBody RoleChangeRequest req) {
        return userRepository.findById(id).map(u -> {
            u.setRole(req.getRole());
            userRepository.save(u);
            Map<String, Object> body = new HashMap<>();
            body.put("message", "role updated");
            return ResponseEntity.ok(body);
        }).orElseGet(() -> {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "User not found");
            return ResponseEntity.status(404).body(body);
        });
    }

}
