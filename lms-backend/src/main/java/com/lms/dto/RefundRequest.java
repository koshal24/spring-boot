package com.lms.dto;

import lombok.Data;
import java.util.Date;

@Data
public class RefundRequest {
    private String purchaseId;
    private String userId;
    private double amount;
    private Date refundDate;
    private String reason;
}
