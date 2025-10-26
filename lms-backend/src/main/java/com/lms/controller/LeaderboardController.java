package com.lms.controller;

import com.lms.model.QuizAttempt;
import com.lms.repository.QuizAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.bson.Document;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/leaderboards")
public class LeaderboardController {
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/top-scores")
    public ResponseEntity<List<Map<String, Object>>> getTopScores() {
        // Use MongoDB aggregation to group quiz attempts by user DBRef id and sum scores, sort descending and limit 10.
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.group("user.$id").sum("score").as("totalScore"),
            Aggregation.sort(Sort.by(Sort.Direction.DESC, "totalScore")),
            Aggregation.limit(10)
        );
        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "quiz_attempts", Document.class);
        List<Map<String, Object>> leaderboard = results.getMappedResults().stream().map(doc -> {
            Map<String, Object> m = new java.util.HashMap<>();
            Object idObj = doc.get("_id");
            m.put("userId", idObj != null ? idObj.toString() : null);
            Object scoreObj = doc.get("totalScore");
            m.put("score", scoreObj instanceof Number ? ((Number) scoreObj).intValue() : 0);
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(leaderboard);
    }
}
