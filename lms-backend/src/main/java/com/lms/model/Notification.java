package com.lms.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.DBRef
    private User user;
    private String message;
    private String type; // e.g., COURSE_UPDATE, FORUM_REPLY, CERTIFICATE_ISSUED
    private boolean read;
    private long timestamp;
}
