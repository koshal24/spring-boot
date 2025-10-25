package com.lms.repository;

import com.lms.model.Educator;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface EducatorRepository extends MongoRepository<Educator, String> {
    Optional<Educator> findByEmail(String email);
}
