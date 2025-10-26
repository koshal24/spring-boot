package com.lms.controller;

import com.lms.model.User;
import com.lms.service.UserService;
import com.lms.service.CourseService;
import com.lms.service.PurchaseService;
import com.lms.service.CourseProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.lms.dto.RoleChangeRequest;
import com.lms.model.Role;
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
    public ResponseEntity<?> getReport(@RequestParam(name = "download", required = false, defaultValue = "false") boolean download) {
        int users = userService.getAllUsers().size();
        int courses = courseService.getAllCourses().size();
        int purchases = purchaseService.getAllPurchases().size();
        int courseProgress = courseProgressService.getAllCourseProgress().size();
        if (!download) {
            Map<String, Object> report = new HashMap<>();
            report.put("users", users);
            report.put("courses", courses);
            report.put("purchases", purchases);
            report.put("courseProgress", courseProgress);
            return ResponseEntity.ok(report);
        }

        // Build CSV
        StringBuilder csv = new StringBuilder();
        csv.append("Metric,Value\n");
        csv.append("Users,").append(users).append("\n");
        csv.append("Courses,").append(courses).append("\n");
        csv.append("Purchases,").append(purchases).append("\n");
        csv.append("CourseProgress,").append(courseProgress).append("\n");

        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.csv");
        headers.setContentType(MediaType.TEXT_PLAIN);
        return ResponseEntity.ok().headers(headers).contentLength(bytes.length).body(bytes);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/users/{id}/role")
    public ResponseEntity<Map<String, Object>> changeUserRole(@PathVariable String id, @Valid @RequestBody RoleChangeRequest req) {
        return userRepository.findById(id).map(u -> {
            try {
                Role r = Role.valueOf(req.getRole());
                u.setRole(r);
                userRepository.save(u);
                Map<String, Object> body = new HashMap<>();
                body.put("message", "role updated");
                return ResponseEntity.ok(body);
            } catch (IllegalArgumentException ex) {
                Map<String, Object> err = new HashMap<>();
                err.put("error", "Invalid role value");
                return ResponseEntity.badRequest().body(err);
            }
        }).orElseGet(() -> {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "User not found");
            return ResponseEntity.status(404).body(body);
        });
    }

}
