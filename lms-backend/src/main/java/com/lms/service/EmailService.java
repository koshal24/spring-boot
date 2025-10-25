package com.lms.service;

public interface EmailService {
    void sendVerificationCode(String toEmail, String code);
}
