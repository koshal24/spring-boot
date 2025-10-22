package com.lms.dto;

import lombok.Data;

@Data
public class CertificateDTO {
    private String id;
    private String userId;
    private String userName;
    private String courseId;
    private String courseTitle;
    private String certificateUrl;
    private long issuedAt;
}
