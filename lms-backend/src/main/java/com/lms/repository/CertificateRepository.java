package com.lms.repository;

import com.lms.model.Certificate;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CertificateRepository extends MongoRepository<Certificate, String> {
    // Query by stored string ids
    List<Certificate> findByUserId(String userId);
    List<Certificate> findByCourseId(String courseId);
}
