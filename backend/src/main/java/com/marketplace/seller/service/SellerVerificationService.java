package com.marketplace.seller.service;

import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.seller.dto.SellerVerificationDTO;
import com.marketplace.seller.entity.SellerVerification;
import com.marketplace.seller.repository.SellerVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class SellerVerificationService {

    @Autowired
    private SellerVerificationRepository verificationRepository;

    @Autowired
    private UserRepository userRepository;

    public SellerVerificationDTO.VerificationResponse requestVerification(
            Long userId, SellerVerificationDTO.VerificationRequest request) {

        if (verificationRepository.existsByUserId(userId)) {
            throw new BadRequestException("Verification request already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SellerVerification verification = SellerVerification.builder()
                .user(user)
                .documentType(request.getDocumentType())
                .documentUrl(request.getDocumentUrl())
                .status("PENDING")
                .build();

        verification = verificationRepository.save(verification);
        return convertToResponse(verification);
    }

    public Page<SellerVerificationDTO.VerificationResponse> getPendingVerifications(Pageable pageable) {
        Page<SellerVerification> verifications = verificationRepository
                .findByStatusOrderByCreatedAtDesc("PENDING", pageable);
        return verifications.map(this::convertToResponse);
    }

    public SellerVerificationDTO.VerificationResponse approveVerification(Long verificationId, Long adminId) {
        SellerVerification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification not found"));

        if (!"PENDING".equals(verification.getStatus())) {
            throw new BadRequestException("Verification already reviewed");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        verification.setStatus("APPROVED");
        verification.setReviewedBy(admin);
        verification.setReviewedAt(LocalDateTime.now());

        verification = verificationRepository.save(verification);
        return convertToResponse(verification);
    }

    public SellerVerificationDTO.VerificationResponse rejectVerification(
            Long verificationId, Long adminId, SellerVerificationDTO.ReviewRequest request) {

        SellerVerification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification not found"));

        if (!"PENDING".equals(verification.getStatus())) {
            throw new BadRequestException("Verification already reviewed");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        verification.setStatus("REJECTED");
        verification.setReviewedBy(admin);
        verification.setReviewedAt(LocalDateTime.now());
        verification.setRejectionReason(request.getRejectionReason());

        verification = verificationRepository.save(verification);
        return convertToResponse(verification);
    }

    private SellerVerificationDTO.VerificationResponse convertToResponse(SellerVerification verification) {
        return SellerVerificationDTO.VerificationResponse.builder()
                .id(verification.getId())
                .userId(verification.getUser().getId())
                .userName(verification.getUser().getFirstName() + " " + verification.getUser().getLastName())
                .documentType(verification.getDocumentType())
                .documentUrl(verification.getDocumentUrl())
                .status(verification.getStatus())
                .reviewedBy(verification.getReviewedBy() != null ? verification.getReviewedBy().getId() : null)
                .reviewedByName(verification.getReviewedBy() != null
                        ? verification.getReviewedBy().getFirstName() + " " + verification.getReviewedBy().getLastName()
                        : null)
                .reviewedAt(verification.getReviewedAt())
                .rejectionReason(verification.getRejectionReason())
                .createdAt(verification.getCreatedAt())
                .build();
    }
}
