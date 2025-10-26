package com.lms.controller;

import com.lms.model.Certificate;
import com.lms.model.User;
import com.lms.model.Course;
import com.lms.dto.CertificateRequest;
import com.lms.dto.CertificateDTO;
import com.lms.repository.UserRepository;
import com.lms.repository.CourseRepository;
import com.lms.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CertificateDTO>> getCertificatesByUser(@PathVariable String userId) {
        List<Certificate> list = certificateService.getCertificatesByUserId(userId);
        List<CertificateDTO> dto = list.stream().map(this::toDto).toList();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CertificateDTO>> getCertificatesByCourse(@PathVariable String courseId) {
        List<Certificate> list = certificateService.getCertificatesByCourseId(courseId);
        List<CertificateDTO> dto = list.stream().map(this::toDto).toList();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateDTO> getCertificateById(@PathVariable String id) {
        Optional<Certificate> cert = certificateService.getCertificateById(id);
        return cert.map(c -> ResponseEntity.ok(toDto(c))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Certificate> issueCertificate(@RequestBody CertificateRequest req) {
        // Resolve user and course IDs to DBRef objects
        var userOpt = userRepository.findById(req.getUserId());
        var courseOpt = courseRepository.findById(req.getCourseId());
        if (userOpt.isEmpty() || courseOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Certificate cert = new Certificate();
        cert.setUser(userOpt.get());
        cert.setCourse(courseOpt.get());
        cert.setCertificateUrl(req.getCertificateUrl());
        cert.setIssuedAt(System.currentTimeMillis());
        Certificate saved = certificateService.saveCertificate(cert);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable String id) {
        certificateService.deleteCertificate(id);
        return ResponseEntity.noContent().build();
    }

    private CertificateDTO toDto(Certificate c) {
        CertificateDTO dto = new CertificateDTO();
        dto.setId(c.getId());
        if (c.getUser() != null) {
            dto.setUserId(c.getUser().getId());
            dto.setUserName(c.getUser().getName());
        }
        if (c.getCourse() != null) {
            dto.setCourseId(c.getCourse().getId());
            dto.setCourseTitle(c.getCourse().getTitle());
        }
        dto.setCertificateUrl(c.getCertificateUrl());
        dto.setIssuedAt(c.getIssuedAt());
        return dto;
    }
}
