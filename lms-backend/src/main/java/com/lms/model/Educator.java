package com.lms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "educators")
public class Educator {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String bio;
    @DBRef
    private List<Course> uploadedCourses;
    private boolean verified = false;
}
