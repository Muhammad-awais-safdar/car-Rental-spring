package com.marketplace.admin.controller;

import com.marketplace.admin.entity.ActivityLog;
import com.marketplace.admin.service.ActivityLogService;
import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/logs")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public class AdminActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ActivityLog>>> getActivityLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ActivityLog> logs = activityLogService.getAllLogs(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Activity logs retrieved successfully", logs));
    }
}
