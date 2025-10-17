package com.lms.controller;

import com.lms.model.CourseProgress;
import com.lms.service.CourseProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/course-progress")
public class CourseProgressController {
    @Autowired
    private CourseProgressService courseProgressService;

    @GetMapping
    public ResponseEntity<List<CourseProgress>> getAllCourseProgress() {
        return ResponseEntity.ok(courseProgressService.getAllCourseProgress());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseProgress> getCourseProgressById(@PathVariable String id) {
        Optional<CourseProgress> cp = courseProgressService.getCourseProgressById(id);
        return cp.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CourseProgress> createCourseProgress(@RequestBody CourseProgress courseProgress) {
        CourseProgress saved = courseProgressService.saveCourseProgress(courseProgress);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseProgress> updateCourseProgress(@PathVariable String id, @RequestBody CourseProgress updatedProgress) {
        Optional<CourseProgress> existing = courseProgressService.getCourseProgressById(id);
        if (existing.isPresent()) {
            CourseProgress cp = existing.get();
            cp.setCompletedLessons(updatedProgress.getCompletedLessons());
            cp.setTotalLessons(updatedProgress.getTotalLessons());
            CourseProgress saved = courseProgressService.saveCourseProgress(cp);
            return ResponseEntity.ok(saved);
        } else {
            CourseProgress saved = courseProgressService.saveCourseProgress(updatedProgress);
            return ResponseEntity.status(201).body(saved);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseProgress(@PathVariable String id) {
        courseProgressService.deleteCourseProgress(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/percentage/{id}")
    public ResponseEntity<Double> getProgressPercentage(@PathVariable String id) {
        Optional<CourseProgress> cp = courseProgressService.getCourseProgressById(id);
        double percent = cp.map(c -> c.getTotalLessons() == 0 ? 0.0 : (c.getCompletedLessons() * 100.0) / c.getTotalLessons()).orElse(0.0);
        return ResponseEntity.ok(percent);
    }

    @GetMapping("/analytics/course/{courseId}")
    public ResponseEntity<Double> getAverageCompletionRate(@PathVariable String courseId) {
        var progresses = courseProgressService.getAllCourseProgress().stream()
            .filter(cp -> courseId.equals(cp.getCourseId()) && cp.getTotalLessons() > 0)
            .collect(toList());
        double avg = progresses.isEmpty() ? 0.0 : progresses.stream()
            .mapToDouble(cp -> (cp.getCompletedLessons() * 100.0) / cp.getTotalLessons())
            .average().orElse(0.0);
        return ResponseEntity.ok(avg);
    }
}
