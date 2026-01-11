package com.marketplace.admin.service;

import com.marketplace.admin.entity.AuditTrail;
import com.marketplace.admin.repository.AuditTrailRepository;
import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuditTrailService {

    @Autowired
    private AuditTrailRepository auditTrailRepository;

    @Autowired
    private UserRepository userRepository;

    public void createAuditTrail(Long userId, String action, String entityType,
            Long entityId, String oldValue, String newValue) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return;

        AuditTrail audit = AuditTrail.builder()
                .user(user)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();

        auditTrailRepository.save(audit);
    }

    public Page<AuditTrail> getEntityAuditTrail(String entityType, Long entityId, Pageable pageable) {
        return auditTrailRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                entityType, entityId, pageable);
    }

    public Page<AuditTrail> getUserAuditTrail(Long userId, Pageable pageable) {
        return auditTrailRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<AuditTrail> getAuditTrailsByAction(String action, Pageable pageable) {
        return auditTrailRepository.findByActionOrderByCreatedAtDesc(action, pageable);
    }

    public Page<AuditTrail> getAllAuditTrails(Pageable pageable) {
        return auditTrailRepository.findAll(pageable);
    }
}
