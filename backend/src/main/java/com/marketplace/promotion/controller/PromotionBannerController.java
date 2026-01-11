package com.marketplace.promotion.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.promotion.dto.PromotionBannerDTO;
import com.marketplace.promotion.service.PromotionBannerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(origins = "*")
public class PromotionBannerController {

    @Autowired
    private PromotionBannerService bannerService;

    @GetMapping("/banners")
    public ResponseEntity<ApiResponse<List<PromotionBannerDTO.BannerResponse>>> getActiveBanners() {
        List<PromotionBannerDTO.BannerResponse> banners = bannerService.getActiveBanners();
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Active banners retrieved successfully", banners));
    }

    @GetMapping("/banners/position/{position}")
    public ResponseEntity<ApiResponse<List<PromotionBannerDTO.BannerResponse>>> getBannersByPosition(
            @PathVariable String position) {
        List<PromotionBannerDTO.BannerResponse> banners = bannerService.getActiveBannersByPosition(position);
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Banners retrieved successfully", banners));
    }

    @GetMapping("/admin/banners")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PromotionBannerDTO.BannerResponse>>> getAllBanners() {
        List<PromotionBannerDTO.BannerResponse> banners = bannerService.getAllBanners();
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "All banners retrieved successfully", banners));
    }

    @PostMapping("/admin/banners")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PromotionBannerDTO.BannerResponse>> createBanner(
            @Valid @RequestBody PromotionBannerDTO.CreateBannerRequest request) {

        PromotionBannerDTO.BannerResponse response = bannerService.createBanner(request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, "Banner created successfully", response),
                HttpStatus.CREATED);
    }

    @PutMapping("/admin/banners/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PromotionBannerDTO.BannerResponse>> updateBanner(
            @PathVariable Long id,
            @Valid @RequestBody PromotionBannerDTO.CreateBannerRequest request) {

        PromotionBannerDTO.BannerResponse response = bannerService.updateBanner(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Banner updated successfully", response));
    }

    @DeleteMapping("/admin/banners/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Banner deleted successfully", null));
    }
}
