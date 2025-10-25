package com.lms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String role; // e.g., ADMIN, EDUCATOR, STUDENT
    private String bio; // Educator bio (optional)
    private List<String> uploadedCourses; // For educators: list of course IDs they uploaded
    private List<String> purchasedCourses;
    // Email verification fields
    private boolean verified = false;
    private String verificationCode;
    private Instant verificationExpiry;
}
