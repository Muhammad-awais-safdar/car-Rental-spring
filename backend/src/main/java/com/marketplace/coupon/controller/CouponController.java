package com.marketplace.coupon.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.coupon.dto.CouponDTO;
import com.marketplace.coupon.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "*")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponDTO.CouponResponse>> createCoupon(
            @Valid @RequestBody CouponDTO.CreateCouponRequest request) {

        CouponDTO.CouponResponse response = couponService.createCoupon(request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, "Coupon created successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<ApiResponse<CouponDTO.ValidateCouponResponse>> validateCoupon(
            @PathVariable String code,
            @RequestParam BigDecimal amount) {

        CouponDTO.ValidateCouponResponse response = couponService.validateCoupon(code, amount);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Coupon validated", response));
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<Void>> applyCoupon(@RequestParam String code) {
        couponService.applyCoupon(code);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Coupon applied successfully", null));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CouponDTO.CouponResponse>>> getAllCoupons() {
        List<CouponDTO.CouponResponse> coupons = couponService.getAllCoupons();

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Coupons retrieved successfully", coupons));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Coupon deleted successfully", null));
    }
}
