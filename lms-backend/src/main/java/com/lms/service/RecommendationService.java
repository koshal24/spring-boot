package com.lms.service;

import com.lms.model.Course;
import com.lms.model.User;
import com.lms.repository.CourseRepository;
import com.lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseService courseService;

    // Recommend courses based on user's interests and progress
    public List<Course> recommendCourses(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();
        List<String> purchased = user.getPurchasedCourses() == null ? List.of() : user.getPurchasedCourses().stream().map(Course::getId).collect(Collectors.toList());
        // Use repository-backed query to fetch top N courses excluding purchased ones
        return courseService.getRecommendedCoursesExcluding(purchased, 5);
    }
}
