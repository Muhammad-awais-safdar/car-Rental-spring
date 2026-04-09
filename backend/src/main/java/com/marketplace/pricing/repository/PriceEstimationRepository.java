package com.marketplace.pricing.repository;

import com.marketplace.pricing.entity.PriceEstimation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceEstimationRepository extends JpaRepository<PriceEstimation, Long> {

    List<PriceEstimation> findByMakeAndModelAndYearOrderByCreatedAtDesc(
            String make, String model, Integer year);

    @Query("SELECT AVG(p.avgPrice) FROM PriceEstimation p WHERE p.make = :make AND p.model = :model AND p.year = :year")
    Double getAveragePriceByMakeModelYear(
            @Param("make") String make,
            @Param("model") String model,
            @Param("year") Integer year);
}
