package com.marketplace.verification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.verification.dto.VerificationDTO;
import com.marketplace.verification.entity.VerificationRequest;
import com.marketplace.verification.repository.VerificationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VerificationService {

    @Autowired
    private VerificationRequestRepository verificationRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public VerificationDTO.VerificationResponse submitVerificationRequest(
            Long userId, VerificationDTO.SubmitVerificationRequest request) {

        // Check if user already has pending request of this type
        if (verificationRequestRepository.existsByUserIdAndVerificationTypeAndStatus(
                userId, request.getVerificationType(), "PENDING")) {
            throw new BadRequestException("You already have a pending " +
                    request.getVerificationType() + " verification request");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Convert document URLs to JSON
        String documentUrlsJson;
        try {
            documentUrlsJson = objectMapper.writeValueAsString(request.getDocumentUrls());
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid document URLs format");
        }

        VerificationRequest verificationRequest = VerificationRequest.builder()
                .user(user)
                .verificationType(request.getVerificationType())
                .status("PENDING")
                .documentUrls(documentUrlsJson)
                .submittedAt(LocalDateTime.now())
                .build();

        verificationRequest = verificationRequestRepository.save(verificationRequest);
        return convertToResponse(verificationRequest);
    }

    public VerificationDTO.VerificationStatusResponse getUserVerificationStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<VerificationRequest> requests = verificationRequestRepository
                .findByUserIdOrderByCreatedAtDesc(userId);

        return VerificationDTO.VerificationStatusResponse.builder()
                .isVerified(user.getIsVerified() != null && user.getIsVerified())
                .verificationLevel(user.getVerificationLevel() != null ? user.getVerificationLevel() : "NONE")
                .verifiedAt(user.getVerifiedAt())
                .requests(requests.stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public List<VerificationDTO.VerificationResponse> getAllPendingRequests() {
        return verificationRequestRepository.findByStatusOrderByCreatedAtDesc("PENDING")
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public VerificationDTO.VerificationResponse approveVerification(
            Long requestId, Long adminId, VerificationDTO.ApproveVerificationRequest request) {

        VerificationRequest verificationRequest = verificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification request not found"));

        if (!"PENDING".equals(verificationRequest.getStatus())) {
            throw new BadRequestException("This verification request has already been processed");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        // Update verification request
        verificationRequest.setStatus("APPROVED");
        verificationRequest.setVerifiedAt(LocalDateTime.now());
        verificationRequest.setVerifiedBy(admin);

        // Update user verification status
        User user = verificationRequest.getUser();
        user.setIsVerified(true);
        user.setVerificationLevel(request.getVerificationLevel());
        user.setVerifiedAt(LocalDateTime.now());
        userRepository.save(user);

        verificationRequest = verificationRequestRepository.save(verificationRequest);
        return convertToResponse(verificationRequest);
    }

    public VerificationDTO.VerificationResponse rejectVerification(
            Long requestId, Long adminId, VerificationDTO.RejectVerificationRequest request) {

        VerificationRequest verificationRequest = verificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification request not found"));

        if (!"PENDING".equals(verificationRequest.getStatus())) {
            throw new BadRequestException("This verification request has already been processed");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        verificationRequest.setStatus("REJECTED");
        verificationRequest.setVerifiedAt(LocalDateTime.now());
        verificationRequest.setVerifiedBy(admin);
        verificationRequest.setRejectionReason(request.getRejectionReason());

        verificationRequest = verificationRequestRepository.save(verificationRequest);
        return convertToResponse(verificationRequest);
    }

    public boolean isUserVerified(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getIsVerified() != null && user.getIsVerified();
    }

    private VerificationDTO.VerificationResponse convertToResponse(VerificationRequest request) {
        List<String> documentUrls = null;
        if (request.getDocumentUrls() != null) {
            try {
                documentUrls = objectMapper.readValue(
                        request.getDocumentUrls(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } catch (JsonProcessingException e) {
                // Handle error silently
            }
        }

        return VerificationDTO.VerificationResponse.builder()
                .id(request.getId())
                .userId(request.getUser().getId())
                .userName(request.getUser().getFirstName() + " " + request.getUser().getLastName())
                .verificationType(request.getVerificationType())
                .status(request.getStatus())
                .documentUrls(documentUrls)
                .submittedAt(request.getSubmittedAt())
                .verifiedAt(request.getVerifiedAt())
                .verifiedByName(request.getVerifiedBy() != null
                        ? request.getVerifiedBy().getFirstName() + " " + request.getVerifiedBy().getLastName()
                        : null)
                .rejectionReason(request.getRejectionReason())
                .build();
    }
}
