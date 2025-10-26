package com.lms.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "quizzes")
public class Quiz {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.DBRef
    private Course course;
    private String title;
    private List<Question> questions;
    private boolean published;

    @Data
    public static class Question {
        private String id;
        private String text;
        private List<String> options;
        private int correctOptionIndex;
    }
}
