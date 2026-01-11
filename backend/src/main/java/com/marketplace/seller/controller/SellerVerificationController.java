package com.marketplace.seller.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.seller.dto.SellerVerificationDTO;
import com.marketplace.seller.service.SellerVerificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/verification")
@CrossOrigin(origins = "*")
public class SellerVerificationController {

    @Autowired
    private SellerVerificationService verificationService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<SellerVerificationDTO.VerificationResponse>> requestVerification(
            @Valid @RequestBody SellerVerificationDTO.VerificationRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        SellerVerificationDTO.VerificationResponse response = verificationService.requestVerification(userId, request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, "Verification request submitted successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<SellerVerificationDTO.VerificationResponse>>> getPendingVerifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SellerVerificationDTO.VerificationResponse> verifications = verificationService
                .getPendingVerifications(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Pending verifications retrieved successfully",
                        verifications));
    }

    @PutMapping("/admin/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SellerVerificationDTO.VerificationResponse>> approveVerification(
            @PathVariable Long id,
            Authentication authentication) {

        Long adminId = Long.parseLong(authentication.getPrincipal().toString());
        SellerVerificationDTO.VerificationResponse response = verificationService.approveVerification(id, adminId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Verification approved successfully", response));
    }

    @PutMapping("/admin/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SellerVerificationDTO.VerificationResponse>> rejectVerification(
            @PathVariable Long id,
            @Valid @RequestBody SellerVerificationDTO.ReviewRequest request,
            Authentication authentication) {

        Long adminId = Long.parseLong(authentication.getPrincipal().toString());
        SellerVerificationDTO.VerificationResponse response = verificationService.rejectVerification(id, adminId,
                request);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Verification rejected successfully", response));
    }
}
