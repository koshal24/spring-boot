package com.lms.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.lms.model.User;
import com.lms.model.Course;

@Data
@Document(collection = "certificates")
public class Certificate {
    @Id
    private String id;
    @DBRef
    private User user;

    @DBRef
    private Course course;
    private String certificateUrl; // Link to download/view certificate
    private long issuedAt;
}
