package com.marketplace.inspection.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.inspection.dto.InspectionDTO;
import com.marketplace.inspection.entity.InspectionReport;
import com.marketplace.inspection.repository.InspectionReportRepository;
import com.marketplace.listing.entity.Listing;
import com.marketplace.listing.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class InspectionService {

    @Autowired
    private InspectionReportRepository inspectionReportRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public InspectionDTO.InspectionResponse createInspectionReport(InspectionDTO.CreateInspectionRequest request) {
        Listing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        String checklistJson = convertToJson(request.getChecklist());
        String photosJson = convertToJson(request.getPhotos());

        InspectionReport report = InspectionReport.builder()
                .listing(listing)
                .inspectorName(request.getInspectorName())
                .inspectionDate(request.getInspectionDate())
                .conditionRating(request.getConditionRating())
                .checklist(checklistJson)
                .photos(photosJson)
                .issuesFound(request.getIssuesFound())
                .recommendations(request.getRecommendations())
                .validUntil(request.getValidUntil())
                .build();

        report = inspectionReportRepository.save(report);
        return convertToResponse(report);
    }

    public Optional<InspectionDTO.InspectionResponse> getValidInspectionForListing(Long listingId) {
        return inspectionReportRepository
                .findValidInspectionByListingId(listingId, LocalDate.now())
                .map(this::convertToResponse);
    }

    public List<InspectionDTO.InspectionResponse> getListingInspectionHistory(Long listingId) {
        return inspectionReportRepository.findByListingIdOrderByInspectionDateDesc(listingId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public void deleteInspectionReport(Long id) {
        if (!inspectionReportRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inspection report not found");
        }
        inspectionReportRepository.deleteById(id);
    }

    private InspectionDTO.InspectionResponse convertToResponse(InspectionReport report) {
        Map<String, Boolean> checklist = null;
        List<String> photos = null;

        try {
            if (report.getChecklist() != null) {
                checklist = objectMapper.readValue(report.getChecklist(), new TypeReference<Map<String, Boolean>>() {
                });
            }
            if (report.getPhotos() != null) {
                photos = objectMapper.readValue(report.getPhotos(), new TypeReference<List<String>>() {
                });
            }
        } catch (JsonProcessingException e) {
            // Handle silently
        }

        return InspectionDTO.InspectionResponse.builder()
                .id(report.getId())
                .listingId(report.getListing().getId())
                .listingTitle(report.getListing().getTitle())
                .inspectorName(report.getInspectorName())
                .inspectionDate(report.getInspectionDate())
                .conditionRating(report.getConditionRating())
                .checklist(checklist)
                .photos(photos)
                .issuesFound(report.getIssuesFound())
                .recommendations(report.getRecommendations())
                .validUntil(report.getValidUntil())
                .isValid(report.getValidUntil().isAfter(LocalDate.now()) ||
                        report.getValidUntil().isEqual(LocalDate.now()))
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
