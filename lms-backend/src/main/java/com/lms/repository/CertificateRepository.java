package com.lms.repository;

import com.lms.model.Certificate;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CertificateRepository extends MongoRepository<Certificate, String> {
    // When using @DBRef User user and Course course, query by nested id fields
    List<Certificate> findByUser_Id(String userId);
    List<Certificate> findByCourse_Id(String courseId);
}
