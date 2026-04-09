package com.marketplace.verification.repository;

import com.marketplace.verification.entity.VerificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Long> {

    List<VerificationRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<VerificationRequest> findByStatusOrderByCreatedAtDesc(String status);

    Long countByUserIdAndStatus(Long userId, String status);

    boolean existsByUserIdAndVerificationTypeAndStatus(Long userId, String verificationType, String status);
}
