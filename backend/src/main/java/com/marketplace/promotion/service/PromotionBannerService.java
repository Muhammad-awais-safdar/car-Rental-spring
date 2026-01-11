package com.marketplace.promotion.service;

import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.promotion.dto.PromotionBannerDTO;
import com.marketplace.promotion.entity.PromotionBanner;
import com.marketplace.promotion.repository.PromotionBannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PromotionBannerService {

    @Autowired
    private PromotionBannerRepository bannerRepository;

    public PromotionBannerDTO.BannerResponse createBanner(PromotionBannerDTO.CreateBannerRequest request) {
        PromotionBanner banner = PromotionBanner.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .linkUrl(request.getLinkUrl())
                .buttonText(request.getButtonText())
                .position(request.getPosition() != null ? request.getPosition() : "HOMEPAGE")
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        banner = bannerRepository.save(banner);
        return convertToResponse(banner);
    }

    public List<PromotionBannerDTO.BannerResponse> getActiveBanners() {
        List<PromotionBanner> banners = bannerRepository.findActiveBanners(LocalDateTime.now());
        return banners.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<PromotionBannerDTO.BannerResponse> getActiveBannersByPosition(String position) {
        List<PromotionBanner> banners = bannerRepository.findActiveBannersByPosition(position, LocalDateTime.now());
        return banners.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<PromotionBannerDTO.BannerResponse> getAllBanners() {
        return bannerRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PromotionBannerDTO.BannerResponse updateBanner(Long bannerId,
            PromotionBannerDTO.CreateBannerRequest request) {
        PromotionBanner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found"));

        banner.setTitle(request.getTitle());
        banner.setDescription(request.getDescription());
        banner.setImageUrl(request.getImageUrl());
        banner.setLinkUrl(request.getLinkUrl());
        banner.setButtonText(request.getButtonText());
        banner.setPosition(request.getPosition());
        banner.setIsActive(request.getIsActive());
        banner.setStartDate(request.getStartDate());
        banner.setEndDate(request.getEndDate());
        banner.setDisplayOrder(request.getDisplayOrder());

        banner = bannerRepository.save(banner);
        return convertToResponse(banner);
    }

    public void deleteBanner(Long bannerId) {
        if (!bannerRepository.existsById(bannerId)) {
            throw new ResourceNotFoundException("Banner not found");
        }
        bannerRepository.deleteById(bannerId);
    }

    private PromotionBannerDTO.BannerResponse convertToResponse(PromotionBanner banner) {
        return PromotionBannerDTO.BannerResponse.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .description(banner.getDescription())
                .imageUrl(banner.getImageUrl())
                .linkUrl(banner.getLinkUrl())
                .buttonText(banner.getButtonText())
                .position(banner.getPosition())
                .isActive(banner.getIsActive())
                .startDate(banner.getStartDate())
                .endDate(banner.getEndDate())
                .displayOrder(banner.getDisplayOrder())
                .createdAt(banner.getCreatedAt())
                .build();
    }
}
