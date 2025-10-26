 package com.lms.controller;

import com.lms.service.AzureStorageService;
import com.lms.service.CourseService;
import com.lms.service.PurchaseService;
import com.lms.service.SubscriptionService;
import com.lms.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    @Autowired
    private AzureStorageService azureStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            String url = azureStorageService.uploadFile(file);
            response.put("url", url);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "Upload failed");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Autowired
    private CourseService courseService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private SubscriptionService subscriptionService;
    @GetMapping("/stream/{courseId}/{userId}/{videoName}")
    public ResponseEntity<Map<String, String>> streamVideo(@PathVariable String courseId, @PathVariable String userId, @PathVariable String videoName) {
        Map<String, String> response = new HashMap<>();
        Course course = courseService.getCourseById(courseId).orElse(null);
        if (course == null) {
            response.put("error", "Course not found.");
            return ResponseEntity.status(404).body(response);
        }
        if (!course.isPaid() || course.getPrice() == 0) {
            // Free course: allow access to anyone
            String videoUrl = "https://your-azure-storage-url/" + videoName; // Replace with actual logic
            response.put("videoUrl", videoUrl);
            return ResponseEntity.ok(response);
        }
        boolean hasPurchase = purchaseService.getPurchasesByUserId(userId).stream()
            .anyMatch(p -> p.getCourse() != null && courseId.equals(p.getCourse().getId()));
        boolean hasSubscription = subscriptionService.getSubscriptionsByUserId(userId).stream()
            .anyMatch(s -> s.isActive());
        boolean canAccess = hasPurchase || hasSubscription;
        if (!canAccess) {
            response.put("error", "Access denied: Please purchase or subscribe to this course.");
            return ResponseEntity.status(403).body(response);
        }
        String videoUrl = "https://your-azure-storage-url/" + videoName; // Replace with actual logic
        response.put("videoUrl", videoUrl);
        return ResponseEntity.ok(response);
    }
}
