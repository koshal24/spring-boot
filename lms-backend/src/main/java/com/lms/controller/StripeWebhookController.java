package com.lms.controller;

import com.lms.model.Purchase;
import com.lms.repository.PurchaseRepository;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

@RestController
@RequestMapping("/api/stripe/webhook")
public class StripeWebhookController {
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @PostMapping
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            if ("payment_intent.succeeded".equals(event.getType())) {
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                if (paymentIntent != null) {
                    Purchase purchase = new Purchase();
                    purchase.setUserId(paymentIntent.getMetadata().get("userId"));
                    purchase.setCourseId(paymentIntent.getMetadata().get("courseId"));
                    purchase.setAmount(paymentIntent.getAmount() / 100.0);
                    purchase.setPurchaseDate(new Date());
                    purchaseRepository.save(purchase);
                }
            }
            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
        }
    }
}
