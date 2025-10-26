package com.lms.repository;

import com.lms.model.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface QuizRepository extends MongoRepository<Quiz, String> {
    List<Quiz> findByCourse_Id(String courseId);
}
