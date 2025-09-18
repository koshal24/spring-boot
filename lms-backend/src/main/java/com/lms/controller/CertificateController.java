package com.lms.controller;

import com.lms.model.Certificate;
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Certificate>> getCertificatesByUser(@PathVariable String userId) {
        return ResponseEntity.ok(certificateService.getCertificatesByUserId(userId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Certificate>> getCertificatesByCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(certificateService.getCertificatesByCourseId(courseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getCertificateById(@PathVariable String id) {
        Optional<Certificate> cert = certificateService.getCertificateById(id);
        return cert.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Certificate> issueCertificate(@RequestBody Certificate certificate) {
        Certificate saved = certificateService.saveCertificate(certificate);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable String id) {
        certificateService.deleteCertificate(id);
        return ResponseEntity.noContent().build();
    }
}
