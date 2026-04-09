package com.marketplace.inspection.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.inspection.dto.InspectionDTO;
import com.marketplace.inspection.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inspections")
@CrossOrigin(origins = "*")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<InspectionDTO.InspectionResponse>> createInspectionReport(
            @RequestBody InspectionDTO.CreateInspectionRequest request) {

        InspectionDTO.InspectionResponse response = inspectionService.createInspectionReport(request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, "Inspection report created successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/listing/{listingId}")
    public ResponseEntity<ApiResponse<InspectionDTO.InspectionResponse>> getListingInspection(
            @PathVariable Long listingId) {

        Optional<InspectionDTO.InspectionResponse> response = inspectionService
                .getValidInspectionForListing(listingId);

        return response.map(inspection -> ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Inspection report retrieved", inspection)))
                .orElseGet(() -> ResponseEntity.ok(
                        ApiResponse.success(Constants.STATUS_SUCCESS, "No valid inspection found", null)));
    }

    @GetMapping("/listing/{listingId}/history")
    public ResponseEntity<ApiResponse<List<InspectionDTO.InspectionResponse>>> getListingInspectionHistory(
            @PathVariable Long listingId) {

        List<InspectionDTO.InspectionResponse> history = inspectionService
                .getListingInspectionHistory(listingId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Inspection history retrieved", history));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteInspectionReport(@PathVariable Long id) {
        inspectionService.deleteInspectionReport(id);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Inspection report deleted", null));
    }
}
