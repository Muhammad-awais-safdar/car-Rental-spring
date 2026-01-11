package com.marketplace.auth.controller;

import com.marketplace.auth.dto.UserDTO;
import com.marketplace.auth.service.UserService;
import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserDTO.ProfileResponse>> getProfile(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        UserDTO.ProfileResponse response = userService.getProfile(userId);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                HttpStatus.OK);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserDTO.ProfileResponse>> updateProfile(
            @Valid @RequestBody UserDTO.UpdateProfileRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        UserDTO.ProfileResponse response = userService.updateProfile(userId, request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Profile updated successfully", response),
                HttpStatus.OK);
    }

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody UserDTO.ChangePasswordRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        userService.changePassword(userId, request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Password changed successfully"),
                HttpStatus.OK);
    }
}
