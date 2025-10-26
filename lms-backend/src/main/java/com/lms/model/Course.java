package com.lms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import com.lms.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
public class Course {
    @Id
    private String id;
    private String title;
    private String description;
    @DBRef
    private User educator; // reference to educator user document (stores user id)

    @DBRef
    private List<User> enrolledUsers; // references to user documents (stores user ids)
    private double price; 
    private boolean paid; 
    // explicit createdAt timestamp used for sorting "new" courses
    private Instant createdAt;
}
