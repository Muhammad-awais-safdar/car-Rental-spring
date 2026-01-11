package com.marketplace.admin.controller;

import com.marketplace.admin.entity.AuditTrail;
import com.marketplace.admin.service.AuditTrailService;
import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AuditTrailController {

    @Autowired
    private AuditTrailService auditTrailService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditTrail>>> getAllAuditTrails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditTrail> auditTrails = auditTrailService.getAllAuditTrails(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Audit trails retrieved successfully", auditTrails));
    }

    @GetMapping("/entity/{type}/{id}")
    public ResponseEntity<ApiResponse<Page<AuditTrail>>> getEntityAuditTrail(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditTrail> auditTrails = auditTrailService.getEntityAuditTrail(type, id, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Entity audit trail retrieved successfully",
                        auditTrails));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<AuditTrail>>> getUserAuditTrail(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditTrail> auditTrails = auditTrailService.getUserAuditTrail(userId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "User audit trail retrieved successfully", auditTrails));
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<ApiResponse<Page<AuditTrail>>> getAuditTrailsByAction(
            @PathVariable String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditTrail> auditTrails = auditTrailService.getAuditTrailsByAction(action, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Audit trails by action retrieved successfully",
                        auditTrails));
    }
}
