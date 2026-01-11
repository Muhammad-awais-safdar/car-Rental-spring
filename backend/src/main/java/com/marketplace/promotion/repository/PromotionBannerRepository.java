package com.marketplace.promotion.repository;

import com.marketplace.promotion.entity.PromotionBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionBannerRepository extends JpaRepository<PromotionBanner, Long> {

    List<PromotionBanner> findByIsActiveTrueOrderByDisplayOrderAsc();

    List<PromotionBanner> findByPositionAndIsActiveTrueOrderByDisplayOrderAsc(String position);

    @Query("SELECT p FROM PromotionBanner p WHERE p.isActive = true " +
            "AND (p.startDate IS NULL OR p.startDate <= :now) " +
            "AND (p.endDate IS NULL OR p.endDate >= :now) " +
            "ORDER BY p.displayOrder ASC")
    List<PromotionBanner> findActiveBanners(LocalDateTime now);

    @Query("SELECT p FROM PromotionBanner p WHERE p.isActive = true " +
            "AND p.position = :position " +
            "AND (p.startDate IS NULL OR p.startDate <= :now) " +
            "AND (p.endDate IS NULL OR p.endDate >= :now) " +
            "ORDER BY p.displayOrder ASC")
    List<PromotionBanner> findActiveBannersByPosition(String position, LocalDateTime now);
}
