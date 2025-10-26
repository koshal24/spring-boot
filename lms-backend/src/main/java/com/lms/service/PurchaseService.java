package com.lms.service;

import com.lms.model.Course;
import com.lms.model.Purchase;
import com.lms.model.User;
import com.lms.repository.CourseRepository;
import com.lms.repository.PurchaseRepository;
import com.lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.bson.Document;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public List<Purchase> getPurchasesByUserId(String userId) {
        return purchaseRepository.findByUser_Id(userId);
    }

    public List<Purchase> getPurchasesByCourseId(String courseId) {
        return purchaseRepository.findByCourse_Id(courseId);
    }

    public Optional<Purchase> getPurchaseById(String id) {
        return purchaseRepository.findById(id);
    }

    public Purchase savePurchase(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    /**
     * Resolve userId and courseId to DBRef references and save a Purchase.
     * Throws IllegalArgumentException if user or course not found.
     */
    public Purchase savePurchaseByIds(String userId, String courseId, Date purchaseDate, double amount) {
        User user = null;
        Course course = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
            if (user == null) throw new IllegalArgumentException("User not found: " + userId);
        }
        if (courseId != null) {
            course = courseRepository.findById(courseId).orElse(null);
            if (course == null) throw new IllegalArgumentException("Course not found: " + courseId);
        }

        Purchase p = new Purchase();
        p.setUser(user);
        p.setCourse(course);
        p.setPurchaseDate(purchaseDate != null ? purchaseDate : new Date());
        p.setAmount(amount);
        return purchaseRepository.save(p);
    }

    public void deletePurchase(String id) {
        purchaseRepository.deleteById(id);
    }

    public double getTotalRevenue() {
        // Use MongoDB aggregation to compute sum(amount) so we don't load all Purchase documents into memory
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group().sum("amount").as("total")
        );
        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "purchases", Document.class);
        Document doc = results.getUniqueMappedResult();
        if (doc == null) return 0.0;
        Object totalObj = doc.get("total");
        if (totalObj instanceof Number) return ((Number) totalObj).doubleValue();
        try {
            return Double.parseDouble(String.valueOf(totalObj));
        } catch (Exception e) {
            return 0.0;
        }
    }
}
