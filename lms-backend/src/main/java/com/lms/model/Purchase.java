package com.lms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
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

    @DBRef
    private User user;

    @DBRef
    private Course course;

    private Date purchaseDate;
    private double amount;
}
