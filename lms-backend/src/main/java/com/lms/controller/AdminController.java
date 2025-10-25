package com.lms.controller;

import com.lms.model.User;
import com.lms.model.Educator;
import com.lms.model.Admin;
import com.lms.service.UserService;
import com.lms.service.EducatorService;
import com.lms.service.AdminService;
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
    private EducatorService educatorService;
    @Autowired
    private AdminService adminService;
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
    public ResponseEntity<Map<String,Object>> getAllUsers() {
        Map<String,Object> result = new HashMap<>();
        result.put("students", userService.getAllUsers());
        result.put("educators", educatorService.getAllEducators());
        result.put("admins", adminService.getAllAdmins());
        return ResponseEntity.ok(result);
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
        String targetRole = req.getRole() == null ? "STUDENT" : req.getRole().toUpperCase();
        Map<String, Object> body = new HashMap<>();
        // Promote student -> educator/admin or demote educator/admin -> student
        if ("ADMIN".equals(targetRole)) {
            return userRepository.findById(id).map(u -> {
                Admin admin = new Admin();
                admin.setName(u.getName());
                admin.setEmail(u.getEmail());
                admin.setPassword(u.getPassword());
                Admin saved = adminService.saveAdmin(admin);
                userRepository.deleteById(id);
                body.put("message", "User promoted to ADMIN");
                body.put("adminId", saved.getId());
                return ResponseEntity.ok(body);
            }).orElseGet(() -> ResponseEntity.status(404).body(Map.of("error","User not found")));
        } else if ("EDUCATOR".equals(targetRole)) {
            return userRepository.findById(id).map(u -> {
                Educator ed = new Educator();
                ed.setName(u.getName());
                ed.setEmail(u.getEmail());
                ed.setPassword(u.getPassword());
                ed.setBio(null);
                Educator saved = educatorService.saveEducator(ed);
                userRepository.deleteById(id);
                body.put("message", "User promoted to EDUCATOR");
                body.put("educatorId", saved.getId());
                return ResponseEntity.ok(body);
            }).orElseGet(() -> ResponseEntity.status(404).body(Map.of("error","User not found")));
        } else if ("STUDENT".equals(targetRole)) {
            // Try to convert educator or admin into student
            return educatorService.getAllEducators().stream().filter(e -> e.getId().equals(id)).findFirst().map(e -> {
                User u = new User();
                u.setName(e.getName());
                u.setEmail(e.getEmail());
                u.setPassword(e.getPassword());
                User saved = userService.saveUser(u);
                educatorService.deleteEducator(id);
                body.put("message", "Educator converted to STUDENT");
                body.put("userId", saved.getId());
                return ResponseEntity.ok(body);
            }).orElseGet(() -> adminService.getAllAdmins().stream().filter(a -> a.getId().equals(id)).findFirst().map(a -> {
                User u = new User();
                u.setName(a.getName());
                u.setEmail(a.getEmail());
                u.setPassword(a.getPassword());
                User saved = userService.saveUser(u);
                adminService.deleteAdmin(id);
                body.put("message", "Admin converted to STUDENT");
                body.put("userId", saved.getId());
                return ResponseEntity.ok(body);
            }).orElseGet(() -> ResponseEntity.status(404).body(Map.of("error","User not found"))));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error","Invalid target role"));
        }
    }

}
