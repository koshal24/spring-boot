package com.lms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

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
    private String role; // e.g., "USER", "ADMIN", "EDUCATOR"
    private String bio; // Educator bio (optional)
    private List<String> uploadedCourses; // For educators: list of course IDs they uploaded
    private List<String> purchasedCourses;
    // Add other fields as needed
}
