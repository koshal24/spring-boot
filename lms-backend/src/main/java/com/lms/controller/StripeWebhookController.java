package com.lms.controller;

import com.lms.model.Course;
import com.lms.model.Purchase;
import com.lms.model.User;
import com.lms.repository.CourseRepository;
import com.lms.repository.PurchaseRepository;
import com.lms.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @PostMapping
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            if ("payment_intent.succeeded".equals(event.getType())) {
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                if (paymentIntent != null) {
                    Purchase purchase = new Purchase();
                    String userId = paymentIntent.getMetadata().get("userId");
                    String courseId = paymentIntent.getMetadata().get("courseId");

                    User user = null;
                    Course course = null;
                    if (userId != null) {
                        user = userRepository.findById(userId).orElse(null);
                    }
                    if (courseId != null) {
                        course = courseRepository.findById(courseId).orElse(null);
                    }

                    purchase.setUser(user);
                    purchase.setCourse(course);
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
