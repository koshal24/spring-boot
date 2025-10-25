package com.lms.service.impl;

import com.lms.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingEmailService implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(LoggingEmailService.class);

    @Override
    public void sendVerificationCode(String toEmail, String code) {
        // In production replace this with a real email sender (JavaMailSender, SendGrid, etc.)
        logger.info("Sending verification code to {}: {}", toEmail, code);
    }
}
