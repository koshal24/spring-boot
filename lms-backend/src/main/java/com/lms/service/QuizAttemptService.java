package com.lms.service;

import com.lms.model.QuizAttempt;
import com.lms.repository.QuizAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class QuizAttemptService {
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    public List<QuizAttempt> getAttemptsByQuizId(String quizId) {
        return quizAttemptRepository.findByQuiz_Id(quizId);
    }

    public List<QuizAttempt> getAttemptsByUserId(String userId) {
        return quizAttemptRepository.findByUser_Id(userId);
    }

    public Optional<QuizAttempt> getAttemptById(String id) {
        return quizAttemptRepository.findById(id);
    }

    public QuizAttempt saveAttempt(QuizAttempt attempt) {
        return quizAttemptRepository.save(attempt);
    }
}
