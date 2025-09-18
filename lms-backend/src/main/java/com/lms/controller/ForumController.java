package com.lms.controller;

import com.lms.model.ForumPost;
import com.lms.service.ForumPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/forums")
public class ForumController {
    @Autowired
    private ForumPostService forumPostService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ForumPost>> getPostsByCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(forumPostService.getPostsByCourseId(courseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ForumPost> getPostById(@PathVariable String id) {
        Optional<ForumPost> post = forumPostService.getPostById(id);
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ForumPost> createPost(@RequestBody ForumPost post) {
        ForumPost saved = forumPostService.savePost(post);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ForumPost> updatePost(@PathVariable String id, @RequestBody ForumPost post) {
        Optional<ForumPost> existing = forumPostService.getPostById(id);
        if (existing.isPresent()) {
            post.setId(id);
            ForumPost updated = forumPostService.savePost(post);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id) {
        forumPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

        // Add a reply to a forum post
        @PostMapping("/{postId}/replies")
        public ResponseEntity<ForumPost> addReply(@PathVariable String postId, @RequestBody ForumPost.Reply reply) {
            ForumPost updated = forumPostService.addReply(postId, reply);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        // Delete a reply from a forum post
        @DeleteMapping("/{postId}/replies/{replyId}")
        public ResponseEntity<ForumPost> deleteReply(@PathVariable String postId, @PathVariable String replyId) {
            ForumPost updated = forumPostService.deleteReply(postId, replyId);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
}
