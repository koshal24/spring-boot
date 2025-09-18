package com.lms.controller;

import com.lms.model.Coupon;
import com.lms.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;

    @GetMapping
    public List<Coupon> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @GetMapping("/{id}")
    public Optional<Coupon> getCouponById(@PathVariable String id) {
        return couponService.getCouponById(id);
    }

    @PostMapping
    public Coupon createCoupon(@RequestBody Coupon coupon) {
        return couponService.saveCoupon(coupon);
    }

    @DeleteMapping("/{id}")
    public void deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
    }
}
