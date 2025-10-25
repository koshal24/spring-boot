package com.lms.repository;

import com.lms.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import com.lms.model.Role;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Add custom query methods if needed
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
}
