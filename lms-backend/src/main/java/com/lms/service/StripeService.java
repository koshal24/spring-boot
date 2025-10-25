package com.lms.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    public StripeService(@Value("${stripe.secret.key}") String secretKey) {
        Stripe.apiKey = secretKey;
    }

    public PaymentIntent createPaymentIntent(long amount, String currency, String userId, String courseId) throws StripeException {
        // Enable Stripe automatic payment methods so Stripe can surface available payment methods
        PaymentIntentCreateParams.AutomaticPaymentMethods automaticPaymentMethods =
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build();

        PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setAutomaticPaymentMethods(automaticPaymentMethods)
                .putMetadata("userId", userId)
                .putMetadata("courseId", courseId);

        // Attach metadata so webhook handlers can identify the user/course for the payment
        if (userId != null) {
            builder.putMetadata("userId", userId);
        }
        if (courseId != null) {
            builder.putMetadata("courseId", courseId);
        }

        PaymentIntentCreateParams params = builder.build();
        return PaymentIntent.create(params);
    }
}
