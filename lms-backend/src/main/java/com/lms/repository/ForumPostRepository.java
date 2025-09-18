package com.lms.repository;

import com.lms.model.ForumPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForumPostRepository extends MongoRepository<ForumPost, String> {
    List<ForumPost> findByCourseId(String courseId);
}
