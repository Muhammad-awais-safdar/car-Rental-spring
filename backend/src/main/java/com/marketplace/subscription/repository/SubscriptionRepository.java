package com.marketplace.subscription.repository;

import com.marketplace.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserIdAndStatus(Long userId, String status);

    List<Subscription> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Subscription> findByEndDateBeforeAndStatus(LocalDateTime date, String status);

    boolean existsByUserIdAndStatus(Long userId, String status);
}
