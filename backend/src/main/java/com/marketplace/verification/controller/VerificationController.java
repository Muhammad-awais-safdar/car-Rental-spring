package com.marketplace.verification.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.verification.dto.VerificationDTO;
import com.marketplace.verification.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/verification")
@CrossOrigin(origins = "*")
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<VerificationDTO.VerificationResponse>> submitVerificationRequest(
            @RequestBody VerificationDTO.SubmitVerificationRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        VerificationDTO.VerificationResponse response = verificationService
                .submitVerificationRequest(userId, request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, "Verification request submitted successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<VerificationDTO.VerificationStatusResponse>> getVerificationStatus(
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        VerificationDTO.VerificationStatusResponse response = verificationService
                .getUserVerificationStatus(userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Verification status retrieved", response));
    }

    @GetMapping("/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<VerificationDTO.VerificationResponse>>> getAllPendingRequests() {
        List<VerificationDTO.VerificationResponse> requests = verificationService.getAllPendingRequests();

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Pending verification requests retrieved", requests));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VerificationDTO.VerificationResponse>> approveVerification(
            @PathVariable Long id,
            @RequestBody VerificationDTO.ApproveVerificationRequest request,
            Authentication authentication) {

        Long adminId = Long.parseLong(authentication.getPrincipal().toString());
        VerificationDTO.VerificationResponse response = verificationService
                .approveVerification(id, adminId, request);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Verification approved successfully", response));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VerificationDTO.VerificationResponse>> rejectVerification(
            @PathVariable Long id,
            @RequestBody VerificationDTO.RejectVerificationRequest request,
            Authentication authentication) {

        Long adminId = Long.parseLong(authentication.getPrincipal().toString());
        VerificationDTO.VerificationResponse response = verificationService
                .rejectVerification(id, adminId, request);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Verification rejected", response));
    }

    @GetMapping("/check/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> checkUserVerified(@PathVariable Long userId) {
        boolean isVerified = verificationService.isUserVerified(userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Verification status checked", isVerified));
    }
}
