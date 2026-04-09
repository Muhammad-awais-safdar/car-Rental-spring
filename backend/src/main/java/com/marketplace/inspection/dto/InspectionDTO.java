package com.marketplace.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class InspectionDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateInspectionRequest {
        private Long listingId;
        private String inspectorName;
        private LocalDate inspectionDate;
        private BigDecimal conditionRating;
        private Map<String, Boolean> checklist;
        private List<String> photos;
        private String issuesFound;
        private String recommendations;
        private LocalDate validUntil;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InspectionResponse {
        private Long id;
        private Long listingId;
        private String listingTitle;
        private String inspectorName;
        private LocalDate inspectionDate;
        private BigDecimal conditionRating;
        private Map<String, Boolean> checklist;
        private List<String> photos;
        private String issuesFound;
        private String recommendations;
        private LocalDate validUntil;
        private boolean isValid;
    }
}
