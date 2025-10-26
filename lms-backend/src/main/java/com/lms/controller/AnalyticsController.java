package com.lms.controller;

import com.lms.model.CourseProgress;
import com.lms.model.QuizAttempt;
import com.lms.model.Purchase;
import com.lms.repository.CourseProgressRepository;
import com.lms.repository.QuizAttemptRepository;
import com.lms.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    @Autowired
    private CourseProgressRepository courseProgressRepository;
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalCourseProgress", courseProgressRepository.count());
        analytics.put("totalQuizAttempts", quizAttemptRepository.count());
        analytics.put("totalPurchases", purchaseService.getAllPurchases().size());
        analytics.put("revenue", purchaseService.getTotalRevenue());
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/course-progress")
    public ResponseEntity<List<CourseProgress>> getAllCourseProgress() {
        return ResponseEntity.ok(courseProgressRepository.findAll());
    }

    @GetMapping("/quiz-performance")
    public ResponseEntity<List<QuizAttempt>> getAllQuizAttempts() {
        return ResponseEntity.ok(quizAttemptRepository.findAll());
    }

    @GetMapping("/revenue")
    public ResponseEntity<Double> getTotalRevenue() {
        double revenue = purchaseService.getTotalRevenue();
        return ResponseEntity.ok(revenue);
    }
}
