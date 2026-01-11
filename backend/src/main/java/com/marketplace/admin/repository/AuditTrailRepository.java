package com.marketplace.admin.repository;

import com.marketplace.admin.entity.AuditTrail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {

    Page<AuditTrail> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType, Long entityId, Pageable pageable);

    Page<AuditTrail> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<AuditTrail> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);
}
