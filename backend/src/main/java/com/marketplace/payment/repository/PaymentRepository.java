package com.marketplace.payment.repository;

import com.marketplace.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Payment> findByUserIdAndStatus(Long userId, String status);

    List<Payment> findByStatus(String status);
}
