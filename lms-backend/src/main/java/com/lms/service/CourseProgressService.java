package com.lms.service;

import com.lms.model.CourseProgress;
import com.lms.repository.CourseProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CourseProgressService {
    @Autowired
    private CourseProgressRepository courseProgressRepository;

    public List<CourseProgress> getAllCourseProgress() {
        return courseProgressRepository.findAll();
    }

    public List<CourseProgress> getProgressByCourseId(String courseId) {
        return courseProgressRepository.findByCourse_Id(courseId);
    }

    public Optional<CourseProgress> getCourseProgressById(String id) {
        return courseProgressRepository.findById(id);
    }

    public CourseProgress saveCourseProgress(CourseProgress courseProgress) {
        return courseProgressRepository.save(courseProgress);
    }

    public void deleteCourseProgress(String id) {
        courseProgressRepository.deleteById(id);
    }
}
