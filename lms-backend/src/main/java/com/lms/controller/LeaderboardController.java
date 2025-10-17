package com.lms.controller;

import com.lms.model.QuizAttempt;
import com.lms.repository.QuizAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leaderboards")
public class LeaderboardController {
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @GetMapping("/top-scores")
    public ResponseEntity<List<Map<String, Object>>> getTopScores() {
        List<QuizAttempt> attempts = quizAttemptRepository.findAll();
        // Group by userId and sum scores
        Map<String, Integer> userScores = attempts.stream()
            .collect(Collectors.groupingBy(QuizAttempt::getUserId, Collectors.summingInt(QuizAttempt::getScore)));
        // Sort and return top 10
        List<Map<String, Object>> leaderboard = userScores.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
            .limit(10)
            .map(e -> {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("userId", e.getKey());
                m.put("score", e.getValue());
                return m;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(leaderboard);
    }
}
