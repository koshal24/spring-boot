package com.lms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "course_progress")
public class CourseProgress {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.DBRef
    private User user;

    @org.springframework.data.mongodb.core.mapping.DBRef
    private Course course;
    private int completedLessons;
    private int totalLessons;
}
