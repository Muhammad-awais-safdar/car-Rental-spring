package com.marketplace.payment.service;

import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.payment.dto.PaymentDTO;
import com.marketplace.payment.entity.Payment;
import com.marketplace.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    public PaymentDTO.PaymentResponse createPayment(Long userId, PaymentDTO.CreatePaymentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Generate mock transaction ID
        String transactionId = "TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
                .user(user)
                .amount(request.getAmount())
                .type(request.getType())
                .status("PENDING")
                .paymentMethod(request.getPaymentMethod())
                .transactionId(transactionId)
                .metadata(request.getMetadata())
                .build();

        payment = paymentRepository.save(payment);
        return convertToResponse(payment);
    }

    public PaymentDTO.PaymentResponse confirmPayment(Long paymentId, PaymentDTO.ConfirmPaymentRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        // In real implementation, verify with payment gateway
        payment.setStatus("COMPLETED");
        payment.setTransactionId(request.getTransactionId());

        payment = paymentRepository.save(payment);
        return convertToResponse(payment);
    }

    public Page<PaymentDTO.PaymentResponse> getPaymentHistory(Long userId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return payments.map(this::convertToResponse);
    }

    public PaymentDTO.PaymentResponse getPaymentDetails(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        return convertToResponse(payment);
    }

    public PaymentDTO.PaymentResponse refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (!"COMPLETED".equals(payment.getStatus())) {
            throw new IllegalStateException("Only completed payments can be refunded");
        }

        payment.setStatus("REFUNDED");
        payment = paymentRepository.save(payment);

        return convertToResponse(payment);
    }

    private PaymentDTO.PaymentResponse convertToResponse(Payment payment) {
        return PaymentDTO.PaymentResponse.builder()
                .id(payment.getId())
                .userId(payment.getUser().getId())
                .amount(payment.getAmount())
                .type(payment.getType())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .metadata(payment.getMetadata())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
