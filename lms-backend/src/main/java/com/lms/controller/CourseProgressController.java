    @GetMapping("/percentage/{id}")
    public double getProgressPercentage(@PathVariable String id) {
        return courseProgressService.getCourseProgressById(id)
            .map(cp -> {
                if (cp.getTotalLessons() == 0) return 0.0;
                return (cp.getCompletedLessons() * 100.0) / cp.getTotalLessons();
            })
            .orElse(0.0);
    }

    @GetMapping("/analytics/course/{courseId}")
    public double getAverageCompletionRate(@PathVariable String courseId) {
        var progresses = courseProgressService.getAllCourseProgress().stream()
            .filter(cp -> courseId.equals(cp.getCourseId()) && cp.getTotalLessons() > 0)
            .toList();
        if (progresses.isEmpty()) return 0.0;
        double avg = progresses.stream()
            .mapToDouble(cp -> (cp.getCompletedLessons() * 100.0) / cp.getTotalLessons())
            .average().orElse(0.0);
        return avg;
    }
package com.lms.controller;

import com.lms.model.CourseProgress;
import com.lms.service.CourseProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/course-progress")
public class CourseProgressController {
    @Autowired
    private CourseProgressService courseProgressService;

    @GetMapping
    public List<CourseProgress> getAllCourseProgress() {
        return courseProgressService.getAllCourseProgress();
    }

    @GetMapping("/{id}")
    public Optional<CourseProgress> getCourseProgressById(@PathVariable String id) {
        return courseProgressService.getCourseProgressById(id);
    }


    @PostMapping
    public CourseProgress createCourseProgress(@RequestBody CourseProgress courseProgress) {
        return courseProgressService.saveCourseProgress(courseProgress);
    }

    @PutMapping("/{id}")
    public CourseProgress updateCourseProgress(@PathVariable String id, @RequestBody CourseProgress updatedProgress) {
        return courseProgressService.getCourseProgressById(id)
            .map(existing -> {
                existing.setCompletedLessons(updatedProgress.getCompletedLessons());
                existing.setTotalLessons(updatedProgress.getTotalLessons());
                return courseProgressService.saveCourseProgress(existing);
            })
            .orElseGet(() -> courseProgressService.saveCourseProgress(updatedProgress));
    }

    @DeleteMapping("/{id}")
    public void deleteCourseProgress(@PathVariable String id) {
        courseProgressService.deleteCourseProgress(id);
    }
}
