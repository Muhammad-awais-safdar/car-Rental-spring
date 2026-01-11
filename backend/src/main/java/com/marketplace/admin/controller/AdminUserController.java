package com.marketplace.admin.controller;

import com.marketplace.admin.dto.AdminUserDTO;
import com.marketplace.admin.service.AdminUserService;
import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminUserDTO.UserListResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserDTO.UserListResponse> users = adminUserService.getAllUsers(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Users retrieved successfully", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUserDTO.UserDetailResponse>> getUserDetails(
            @PathVariable Long id) {

        AdminUserDTO.UserDetailResponse user = adminUserService.getUserDetails(id);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "User details retrieved successfully", user));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<ApiResponse<Void>> blockUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserDTO.BlockUserRequest request,
            Authentication authentication) {

        Long adminId = Long.parseLong(authentication.getPrincipal().toString());
        adminUserService.blockUser(id, adminId, request);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "User blocked successfully", null));
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<ApiResponse<Void>> unblockUser(@PathVariable Long id) {
        adminUserService.unblockUser(id);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "User unblocked successfully", null));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserDTO.UpdateRoleRequest request) {

        adminUserService.updateUserRoles(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "User roles updated successfully", null));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminUserDTO.UserStatistics>> getUserStatistics() {
        AdminUserDTO.UserStatistics stats = adminUserService.getUserStatistics();

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Statistics retrieved successfully", stats));
    }
}
