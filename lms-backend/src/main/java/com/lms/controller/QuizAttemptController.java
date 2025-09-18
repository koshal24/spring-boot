package com.lms.controller;

import com.lms.model.QuizAttempt;
import com.lms.service.QuizAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quiz-attempts")
public class QuizAttemptController {
    @Autowired
    private QuizAttemptService quizAttemptService;

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizAttempt>> getAttemptsByQuiz(@PathVariable String quizId) {
        return ResponseEntity.ok(quizAttemptService.getAttemptsByQuizId(quizId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuizAttempt>> getAttemptsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(quizAttemptService.getAttemptsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizAttempt> getAttemptById(@PathVariable String id) {
        Optional<QuizAttempt> attempt = quizAttemptService.getAttemptById(id);
        return attempt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<QuizAttempt> submitAttempt(@RequestBody QuizAttempt attempt) {
        QuizAttempt saved = quizAttemptService.saveAttempt(attempt);
        return ResponseEntity.ok(saved);
    }
}
