package com.marketplace.coupon.service;

import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.coupon.dto.CouponDTO;
import com.marketplace.coupon.entity.Coupon;
import com.marketplace.coupon.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public CouponDTO.CouponResponse createCoupon(CouponDTO.CreateCouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Coupon code already exists");
        }

        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minAmount(request.getMinAmount())
                .maxUses(request.getMaxUses())
                .currentUses(0)
                .expiresAt(request.getExpiresAt())
                .isActive(true)
                .build();

        coupon = couponRepository.save(coupon);
        return convertToResponse(coupon);
    }

    public CouponDTO.ValidateCouponResponse validateCoupon(String code, BigDecimal amount) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElse(null);

        if (coupon == null) {
            return CouponDTO.ValidateCouponResponse.builder()
                    .valid(false)
                    .message("Invalid coupon code")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        if (!coupon.getIsActive()) {
            return CouponDTO.ValidateCouponResponse.builder()
                    .valid(false)
                    .message("Coupon is not active")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
            return CouponDTO.ValidateCouponResponse.builder()
                    .valid(false)
                    .message("Coupon has expired")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        if (coupon.getMaxUses() != null && coupon.getCurrentUses() >= coupon.getMaxUses()) {
            return CouponDTO.ValidateCouponResponse.builder()
                    .valid(false)
                    .message("Coupon usage limit reached")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        if (coupon.getMinAmount() != null && amount.compareTo(coupon.getMinAmount()) < 0) {
            return CouponDTO.ValidateCouponResponse.builder()
                    .valid(false)
                    .message("Minimum amount not met")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        BigDecimal discountAmount = calculateDiscount(coupon, amount);

        return CouponDTO.ValidateCouponResponse.builder()
                .valid(true)
                .message("Coupon is valid")
                .discountAmount(discountAmount)
                .coupon(convertToResponse(coupon))
                .build();
    }

    public void applyCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        coupon.setCurrentUses(coupon.getCurrentUses() + 1);
        couponRepository.save(coupon);
    }

    public List<CouponDTO.CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void deleteCoupon(Long couponId) {
        if (!couponRepository.existsById(couponId)) {
            throw new ResourceNotFoundException("Coupon not found");
        }
        couponRepository.deleteById(couponId);
    }

    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal amount) {
        if ("PERCENTAGE".equals(coupon.getDiscountType())) {
            return amount.multiply(coupon.getDiscountValue()).divide(new BigDecimal("100"));
        } else if ("FIXED_AMOUNT".equals(coupon.getDiscountType())) {
            return coupon.getDiscountValue();
        }
        return BigDecimal.ZERO;
    }

    private CouponDTO.CouponResponse convertToResponse(Coupon coupon) {
        return CouponDTO.CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minAmount(coupon.getMinAmount())
                .maxUses(coupon.getMaxUses())
                .currentUses(coupon.getCurrentUses())
                .expiresAt(coupon.getExpiresAt())
                .isActive(coupon.getIsActive())
                .createdAt(coupon.getCreatedAt())
                .build();
    }
}
