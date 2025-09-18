package com.lms.service;

import com.lms.model.ForumPost;
import com.lms.repository.ForumPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ForumPostService {
    @Autowired
    private ForumPostRepository forumPostRepository;

    public List<ForumPost> getPostsByCourseId(String courseId) {
        return forumPostRepository.findByCourseId(courseId);
    }

    public Optional<ForumPost> getPostById(String id) {
        return forumPostRepository.findById(id);
    }

    public ForumPost savePost(ForumPost post) {
        return forumPostRepository.save(post);
    }

    public void deletePost(String id) {
        forumPostRepository.deleteById(id);
    }

        // Add a reply to a forum post
        public ForumPost addReply(String postId, ForumPost.Reply reply) {
            Optional<ForumPost> postOpt = forumPostRepository.findById(postId);
            if (postOpt.isPresent()) {
                ForumPost post = postOpt.get();
                post.getReplies().add(reply);
                return forumPostRepository.save(post);
            }
            return null;
        }

        // Delete a reply from a forum post
        public ForumPost deleteReply(String postId, String replyId) {
            Optional<ForumPost> postOpt = forumPostRepository.findById(postId);
            if (postOpt.isPresent()) {
                ForumPost post = postOpt.get();
                post.getReplies().removeIf(r -> r.getId().equals(replyId));
                return forumPostRepository.save(post);
            }
            return null;
        }
}
