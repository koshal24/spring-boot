package com.lms.repository;

import com.lms.model.QuizAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface QuizAttemptRepository extends MongoRepository<QuizAttempt, String> {
    List<QuizAttempt> findByQuizId(String quizId);
    List<QuizAttempt> findByUserId(String userId);
}
