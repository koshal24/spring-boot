package com.lms.repository;

import com.lms.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Deprecated adapter repository kept only for backward compatibility.
 * Educators are now represented by `User` with `Role.EDUCATOR`. Prefer using `UserRepository`.
 */
@Deprecated
@Repository
public interface EducatorRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
