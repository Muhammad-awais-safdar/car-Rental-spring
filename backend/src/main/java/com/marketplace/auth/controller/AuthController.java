package com.marketplace.auth.controller;

import com.marketplace.auth.dto.AuthDTO;
import com.marketplace.auth.service.AuthService;
import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDTO.LoginResponse>> register(
            @Valid @RequestBody AuthDTO.RegisterRequest request) {

        AuthDTO.LoginResponse response = authService.register(request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, Constants.MSG_REGISTER_SUCCESS, response),
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDTO.LoginResponse>> login(
            @Valid @RequestBody AuthDTO.LoginRequest request) {

        AuthDTO.LoginResponse response = authService.login(request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_LOGIN_SUCCESS, response),
                HttpStatus.OK);
    }
}
