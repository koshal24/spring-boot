package com.lms.controller;


import com.lms.model.Subscription;
import com.lms.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping
    public List<Subscription> getAllSubscriptions() {
        return subscriptionService.getAllSubscriptions();
    }

    @GetMapping("/{id}")
    public Optional<Subscription> getSubscriptionById(@PathVariable String id) {
        return subscriptionService.getSubscriptionById(id);
    }

    @PostMapping
    public Subscription createSubscription(@RequestBody Subscription subscription) {
        return subscriptionService.saveSubscription(subscription);
    }

    @DeleteMapping("/{id}")
    public void deleteSubscription(@PathVariable String id) {
        subscriptionService.deleteSubscription(id);
    }
}
