package com.marketplace.auth.repository;

import com.marketplace.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmailOrPhone(String email, String phone);

    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);

    // Admin queries
    List<User> findByIsBlocked(Boolean isBlocked);

    long countByIsBlocked(Boolean isBlocked);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByCreatedAtAfter(LocalDateTime date);
}
