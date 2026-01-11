package com.marketplace.auth.repository;

import com.marketplace.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmailOrPhone(String email, String phone);

    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);

    // Admin queries
    long countByIsBlocked(Boolean isBlocked);

    long countByCreatedAtAfter(LocalDateTime date);
}
