package com.lms.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "quiz_attempts")
public class QuizAttempt {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.DBRef
    private Quiz quiz;

    @org.springframework.data.mongodb.core.mapping.DBRef
    private User user;
    private List<Integer> selectedOptions; // index per question
    private int score;
    private long timestamp;
}
