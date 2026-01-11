package com.marketplace.subscription.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.subscription.dto.SubscriptionDTO;
import com.marketplace.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "*")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<SubscriptionDTO.PlanDetails>>> getAllPlans() {
        List<SubscriptionDTO.PlanDetails> plans = subscriptionService.getAllPlans();
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Plans retrieved successfully", plans));
    }

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<SubscriptionDTO.SubscriptionResponse>> subscribe(
            @Valid @RequestBody SubscriptionDTO.SubscribeRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        SubscriptionDTO.SubscriptionResponse response = subscriptionService.subscribe(userId, request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, "Subscribed successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<SubscriptionDTO.SubscriptionResponse>> getCurrentSubscription(
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        SubscriptionDTO.SubscriptionResponse response = subscriptionService.getCurrentSubscription(userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Current subscription retrieved successfully", response));
    }

    @PutMapping("/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelSubscription(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        subscriptionService.cancelSubscription(userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Subscription cancelled successfully", null));
    }

    @PutMapping("/renew")
    public ResponseEntity<ApiResponse<SubscriptionDTO.SubscriptionResponse>> renewSubscription(
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        SubscriptionDTO.SubscriptionResponse response = subscriptionService.renewSubscription(userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Subscription renewed successfully", response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<SubscriptionDTO.SubscriptionResponse>>> getSubscriptionHistory(
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        List<SubscriptionDTO.SubscriptionResponse> history = subscriptionService.getSubscriptionHistory(userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Subscription history retrieved successfully", history));
    }
}
