package com.lms.repository;

import com.lms.model.QuizAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface QuizAttemptRepository extends MongoRepository<QuizAttempt, String> {
    List<QuizAttempt> findByQuiz_Id(String quizId);
    List<QuizAttempt> findByUser_Id(String userId);
}
