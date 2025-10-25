package com.lms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.time.Instant;
import org.springframework.data.mongodb.core.mapping.DBRef;
import com.lms.model.Role;
import com.lms.model.Course;

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
    private Role role = Role.STUDENT; // Role for this account (enum)
    private String bio; // Educator bio (optional)
    @DBRef
    private List<Course> uploadedCourses; // references to courses uploaded by educator (DBRef -> course ids)
    @DBRef
    private List<Course> purchasedCourses; // Students: reference to purchased courses (stored as DBRefs -> course ids)
    // Email verification fields
    private boolean verified = false;
    private String verificationCode;
    private Instant verificationExpiry;
}
