package com.marketplace.pricing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.listing.entity.Listing;
import com.marketplace.listing.repository.ListingRepository;
import com.marketplace.pricing.dto.PricingDTO;
import com.marketplace.pricing.entity.PriceEstimation;
import com.marketplace.pricing.repository.PriceEstimationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PriceEstimationService {

    @Autowired
    private PriceEstimationRepository priceEstimationRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public PricingDTO.PriceEstimationResponse estimateVehiclePrice(PricingDTO.PriceEstimationRequest request) {
        // Get similar vehicles from database
        List<Listing> similarVehicles = listingRepository.findByMakeAndModelAndYearBetween(
                request.getMake(),
                request.getModel(),
                request.getYear() - 2,
                request.getYear() + 2);

        // Calculate base price from similar vehicles
        BigDecimal basePrice = calculateBasePrice(similarVehicles);

        // Apply depreciation based on age
        BigDecimal depreciationFactor = calculateDepreciation(request.getYear());

        // Apply mileage adjustment
        BigDecimal mileageAdjustment = calculateMileageAdjustment(request.getMileage());

        // Apply condition factor
        BigDecimal conditionFactor = request.getConditionRating().divide(BigDecimal.valueOf(5), 2,
                RoundingMode.HALF_UP);

        // Calculate final price
        BigDecimal estimatedPrice = basePrice
                .multiply(depreciationFactor)
                .multiply(mileageAdjustment)
                .multiply(conditionFactor);

        BigDecimal minPrice = estimatedPrice.multiply(BigDecimal.valueOf(0.85)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal maxPrice = estimatedPrice.multiply(BigDecimal.valueOf(1.15)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal avgPrice = estimatedPrice.setScale(2, RoundingMode.HALF_UP);

        // Save estimation
        Map<String, Object> marketData = new HashMap<>();
        marketData.put("similarVehiclesCount", similarVehicles.size());
        marketData.put("averageMarketPrice", basePrice);
        marketData.put("depreciationFactor", depreciationFactor);
        marketData.put("mileageAdjustment", mileageAdjustment);
        marketData.put("conditionFactor", conditionFactor);

        String marketDataJson = convertToJson(marketData);

        PriceEstimation estimation = PriceEstimation.builder()
                .make(request.getMake())
                .model(request.getModel())
                .year(request.getYear())
                .mileage(request.getMileage())
                .conditionRating(request.getConditionRating())
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .avgPrice(avgPrice)
                .marketData(marketDataJson)
                .build();

        priceEstimationRepository.save(estimation);

        // Get similar vehicle listings
        List<PricingDTO.SimilarVehicle> similarVehicleList = similarVehicles.stream()
                .limit(5)
                .map(this::convertToSimilarVehicle)
                .collect(Collectors.toList());

        return PricingDTO.PriceEstimationResponse.builder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .avgPrice(avgPrice)
                .suggestedPrice(avgPrice)
                .priceRange(formatPriceRange(minPrice, maxPrice))
                .marketData(marketData)
                .similarVehicles(similarVehicleList)
                .build();
    }

    public List<PricingDTO.SimilarVehicle> getSimilarVehiclePrices(Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        List<Listing> similarVehicles = listingRepository.findByMakeAndModelAndYearBetween(
                listing.getMake(),
                listing.getModel(),
                listing.getYear() - 2,
                listing.getYear() + 2);

        return similarVehicles.stream()
                .filter(l -> !l.getId().equals(listingId))
                .limit(10)
                .map(this::convertToSimilarVehicle)
                .collect(Collectors.toList());
    }

    public PricingDTO.MarketTrendResponse getMarketTrends(String make, String model, Integer year) {
        List<Listing> listings = listingRepository.findByMakeAndModelAndYear(make, model, year);

        BigDecimal averagePrice = listings.stream()
                .map(Listing::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(listings.size()), 2, RoundingMode.HALF_UP);

        // Get historical average
        Double historicalAvg = priceEstimationRepository.getAveragePriceByMakeModelYear(make, model, year);
        BigDecimal priceChange = BigDecimal.ZERO;
        String trend = "STABLE";

        if (historicalAvg != null) {
            BigDecimal historical = BigDecimal.valueOf(historicalAvg);
            priceChange = averagePrice.subtract(historical);
            if (priceChange.compareTo(BigDecimal.valueOf(500)) > 0) {
                trend = "UP";
            } else if (priceChange.compareTo(BigDecimal.valueOf(-500)) < 0) {
                trend = "DOWN";
            }
        }

        return PricingDTO.MarketTrendResponse.builder()
                .make(make)
                .model(model)
                .year(year)
                .averagePrice(averagePrice)
                .priceChange(priceChange)
                .trend(trend)
                .totalListings(listings.size())
                .build();
    }

    private BigDecimal calculateBasePrice(List<Listing> similarVehicles) {
        if (similarVehicles.isEmpty()) {
            return BigDecimal.valueOf(15000); // Default base price
        }

        return similarVehicles.stream()
                .map(Listing::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(similarVehicles.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDepreciation(Integer year) {
        int currentYear = Year.now().getValue();
        int age = currentYear - year;

        // 15% depreciation per year for first 5 years, then 10% per year
        double depreciationRate = age <= 5 ? 0.15 : 0.10;
        double totalDepreciation = Math.min(age * depreciationRate, 0.80); // Max 80% depreciation

        return BigDecimal.valueOf(1 - totalDepreciation);
    }

    private BigDecimal calculateMileageAdjustment(Integer mileage) {
        // Adjust based on mileage (average 12,000 miles per year)
        if (mileage < 50000) {
            return BigDecimal.valueOf(1.05); // Low mileage bonus
        } else if (mileage < 100000) {
            return BigDecimal.valueOf(1.00); // Average mileage
        } else if (mileage < 150000) {
            return BigDecimal.valueOf(0.90); // High mileage penalty
        } else {
            return BigDecimal.valueOf(0.75); // Very high mileage penalty
        }
    }

    private String formatPriceRange(BigDecimal min, BigDecimal max) {
        return String.format("$%,.2f - $%,.2f", min, max);
    }

    private PricingDTO.SimilarVehicle convertToSimilarVehicle(Listing listing) {
        return PricingDTO.SimilarVehicle.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .price(listing.getPrice())
                .year(listing.getYear())
                .mileage(listing.getMileage())
                .location(listing.getLocation())
                .build();
    }

    private String convertToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
