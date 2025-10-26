package com.lms.controller;

import com.lms.model.Course;
import com.lms.model.User;
import com.lms.service.CourseService;
import com.lms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;

    @GetMapping("/my-courses")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<Map<String, List<Course>>> getMyCourses() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of());
        }

    List<Course> enrolled = courseService.getCoursesByEnrolledUserId(user.getId());

    List<String> purchasedIds = user.getPurchasedCourses() == null ? List.of() : user.getPurchasedCourses().stream().map(Course::getId).collect(Collectors.toList());
    List<Course> purchased = courseService.getCoursesByIds(purchasedIds);

        return ResponseEntity.ok(Map.of("enrolled", enrolled, "purchased", purchased));
    }
}
