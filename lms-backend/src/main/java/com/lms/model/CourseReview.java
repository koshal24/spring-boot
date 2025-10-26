package com.lms.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "course_reviews")
public class CourseReview {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.DBRef
    private Course course;

    @org.springframework.data.mongodb.core.mapping.DBRef
    private User user;
    private int rating; // 1-5
    private String comment;
    private long timestamp;
}
