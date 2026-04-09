package com.marketplace.pricing.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.pricing.dto.PricingDTO;
import com.marketplace.pricing.service.PriceEstimationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pricing")
@CrossOrigin(origins = "*")
public class PriceEstimationController {

    @Autowired
    private PriceEstimationService priceEstimationService;

    @PostMapping("/estimate")
    public ResponseEntity<ApiResponse<PricingDTO.PriceEstimationResponse>> estimatePrice(
            @RequestBody PricingDTO.PriceEstimationRequest request) {

        PricingDTO.PriceEstimationResponse response = priceEstimationService.estimateVehiclePrice(request);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Price estimated successfully", response));
    }

    @GetMapping("/similar/{listingId}")
    public ResponseEntity<ApiResponse<List<PricingDTO.SimilarVehicle>>> getSimilarVehiclePrices(
            @PathVariable Long listingId) {

        List<PricingDTO.SimilarVehicle> similarVehicles = priceEstimationService
                .getSimilarVehiclePrices(listingId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Similar vehicles retrieved", similarVehicles));
    }

    @GetMapping("/trends")
    public ResponseEntity<ApiResponse<PricingDTO.MarketTrendResponse>> getMarketTrends(
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam Integer year) {

        PricingDTO.MarketTrendResponse response = priceEstimationService.getMarketTrends(make, model, year);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Market trends retrieved", response));
    }
}
