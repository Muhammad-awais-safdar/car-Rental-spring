package com.marketplace.seller.repository;

import com.marketplace.seller.entity.SellerVerification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerVerificationRepository extends JpaRepository<SellerVerification, Long> {

    Optional<SellerVerification> findByUserId(Long userId);

    Page<SellerVerification> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    boolean existsByUserId(Long userId);
}
