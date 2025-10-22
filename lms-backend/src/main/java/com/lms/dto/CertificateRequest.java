package com.lms.dto;

import lombok.Data;

@Data
public class CertificateRequest {
    private String userId;
    private String courseId;
    private String certificateUrl;
}
