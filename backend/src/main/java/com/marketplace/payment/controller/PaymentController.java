package com.marketplace.payment.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.payment.dto.PaymentDTO;
import com.marketplace.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PaymentDTO.PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentDTO.CreatePaymentRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        PaymentDTO.PaymentResponse response = paymentService.createPayment(userId, request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, "Payment created successfully", response),
                HttpStatus.CREATED);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<PaymentDTO.PaymentResponse>> confirmPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentDTO.ConfirmPaymentRequest request) {

        PaymentDTO.PaymentResponse response = paymentService.confirmPayment(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Payment confirmed successfully", response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<PaymentDTO.PaymentResponse>>> getPaymentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentDTO.PaymentResponse> history = paymentService.getPaymentHistory(userId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Payment history retrieved successfully", history));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDTO.PaymentResponse>> getPaymentDetails(@PathVariable Long id) {
        PaymentDTO.PaymentResponse response = paymentService.getPaymentDetails(id);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Payment details retrieved successfully", response));
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentDTO.PaymentResponse>> refundPayment(@PathVariable Long id) {
        PaymentDTO.PaymentResponse response = paymentService.refundPayment(id);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Payment refunded successfully", response));
    }
}
