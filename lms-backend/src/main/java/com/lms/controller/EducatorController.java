package com.lms.controller;

import com.lms.model.Course;
import com.lms.model.Educator;
import com.lms.service.CourseService;
import com.lms.service.EducatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/educator")
public class EducatorController {
    @Autowired
    private EducatorService educatorService;
    @Autowired
    private CourseService courseService;

    @PostMapping("/courses")
    @PreAuthorize("hasAuthority('EDUCATOR')")
    public ResponseEntity<Course> uploadCourse(@RequestBody Course course, @RequestParam String educatorId) {
        course.setEducatorId(educatorId);
        Course savedCourse = courseService.saveCourse(course);
        Optional<Educator> educatorOpt = educatorService.getEducatorById(educatorId);
        if (educatorOpt.isPresent()) {
            Educator educator = educatorOpt.get();
            if (educator.getUploadedCourses() != null) {
                educator.getUploadedCourses().add(savedCourse.getId());
            } else {
                educator.setUploadedCourses(List.of(savedCourse.getId()));
            }
            educatorService.saveEducator(educator);
        }
        return ResponseEntity.ok(savedCourse);
    }

    @GetMapping("/courses")
    @PreAuthorize("hasAuthority('EDUCATOR')")
    public ResponseEntity<List<Course>> getMyCourses(@RequestParam String educatorId) {
        List<Course> courses = courseService.getAllCourses().stream()
                .filter(c -> educatorId.equals(c.getEducatorId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/courses/enrollments")
    @PreAuthorize("hasAuthority('EDUCATOR')")
    public ResponseEntity<List<Object>> getCourseEnrollments(@RequestParam String educatorId) {
        List<Course> courses = courseService.getAllCourses().stream()
                .filter(c -> educatorId.equals(c.getEducatorId()))
                .collect(Collectors.toList());
        List<Object> enrollments = courses.stream().map(c -> {
            return new Object() {
                public String courseId = c.getId();
                public String title = c.getTitle();
                public int enrolledCount = c.getEnrolledUserIds() != null ? c.getEnrolledUserIds().size() : 0;
            };
        }).collect(Collectors.toList());
        return ResponseEntity.ok(enrollments);
    }
}
