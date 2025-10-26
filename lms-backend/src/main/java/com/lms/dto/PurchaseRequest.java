package com.lms.dto;

import lombok.Data;
import java.util.Date;

@Data
public class PurchaseRequest {
    private String userId;
    private String courseId;
    private Date purchaseDate;
    private double amount;
}
