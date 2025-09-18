package com.lms.controller;

import com.lms.service.UserService;
import com.lms.service.CourseService;
import com.lms.service.PurchaseService;
import com.lms.service.CourseProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/admin/report")
public class ReportController {
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private CourseProgressService courseProgressService;

    @GetMapping("/download")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<byte[]> downloadReport() {
        StringBuilder csv = new StringBuilder();
        csv.append("Type,Count\n");
        csv.append("Users,").append(userService.getAllUsers().size()).append("\n");
        csv.append("Courses,").append(courseService.getAllCourses().size()).append("\n");
        csv.append("Purchases,").append(purchaseService.getAllPurchases().size()).append("\n");
        csv.append("CourseProgress,").append(courseProgressService.getAllCourseProgress().size()).append("\n");
        byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.csv");
        headers.setContentType(MediaType.TEXT_PLAIN);
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }
}
