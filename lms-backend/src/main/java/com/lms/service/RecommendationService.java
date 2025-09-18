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

    // Recommend courses based on user's interests and progress
    public List<Course> recommendCourses(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();
        List<String> purchased = user.getPurchasedCourses();
        // Recommend courses not yet purchased by user
        return courseRepository.findAll().stream()
                .filter(course -> purchased == null || !purchased.contains(course.getId()))
                .limit(5)
                .collect(Collectors.toList());
    }
}
