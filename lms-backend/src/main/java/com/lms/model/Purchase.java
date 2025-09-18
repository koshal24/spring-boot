package com.lms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "purchases")
public class Purchase {
    @Id
    private String id;
    private String userId;
    private String courseId;
    private Date purchaseDate;
    private double amount;
    // Add other fields as needed
}
