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
@Document(collection = "refunds")
public class Refund {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.DBRef
    private Purchase purchase;

    @org.springframework.data.mongodb.core.mapping.DBRef
    private User user;
    private double amount;
    private Date refundDate;
    private String reason;
}
