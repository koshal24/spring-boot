package com.lms.service;

import com.lms.model.Certificate;
import com.lms.model.User;
import com.lms.model.Course;
import com.lms.repository.CertificateRepository;
import com.lms.repository.UserRepository;
import com.lms.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CertificateService {
    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Certificate> getCertificatesByUserId(String userId) {
        return certificateRepository.findByUser_Id(userId);
    }

    public List<Certificate> getCertificatesByCourseId(String courseId) {
        return certificateRepository.findByCourse_Id(courseId);
    }

    public Optional<Certificate> getCertificateById(String id) {
        return certificateRepository.findById(id);
    }

    public Certificate saveCertificate(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    public void deleteCertificate(String id) {
        certificateRepository.deleteById(id);
    }
}
