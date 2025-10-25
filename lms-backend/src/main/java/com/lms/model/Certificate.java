package com.lms.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "certificates")
public class Certificate {
    @Id
    private String id;
    // store references by id only
    private String userId;
    private String courseId;
    private String certificateUrl; // Link to download/view certificate
    private long issuedAt;
}
