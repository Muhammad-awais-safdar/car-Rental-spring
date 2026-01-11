package com.marketplace.subscription.service;

import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.subscription.dto.SubscriptionDTO;
import com.marketplace.subscription.entity.Subscription;
import com.marketplace.subscription.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<SubscriptionDTO.PlanDetails> getAllPlans() {
        List<SubscriptionDTO.PlanDetails> plans = new ArrayList<>();

        plans.add(SubscriptionDTO.PlanDetails.builder()
                .name("FREE")
                .displayName("Free Plan")
                .price(0.0)
                .listingLimit(1)
                .description("Basic listing for 30 days")
                .featured(false)
                .prioritySupport(false)
                .build());

        plans.add(SubscriptionDTO.PlanDetails.builder()
                .name("BASIC")
                .displayName("Basic Plan")
                .price(9.99)
                .listingLimit(5)
                .description("5 listings with standard support")
                .featured(false)
                .prioritySupport(false)
                .build());

        plans.add(SubscriptionDTO.PlanDetails.builder()
                .name("PRO")
                .displayName("Pro Plan")
                .price(29.99)
                .listingLimit(20)
                .description("20 listings with priority support and featured badge")
                .featured(true)
                .prioritySupport(true)
                .build());

        plans.add(SubscriptionDTO.PlanDetails.builder()
                .name("PREMIUM")
                .displayName("Premium Plan")
                .price(99.99)
                .listingLimit(999)
                .description("Unlimited listings with premium support and top placement")
                .featured(true)
                .prioritySupport(true)
                .build());

        return plans;
    }

    public SubscriptionDTO.SubscriptionResponse subscribe(Long userId, SubscriptionDTO.SubscribeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user already has active subscription
        if (subscriptionRepository.existsByUserIdAndStatus(userId, "ACTIVE")) {
            throw new BadRequestException("User already has an active subscription");
        }

        // Calculate dates
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(1);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(request.getPlan())
                .startDate(startDate)
                .endDate(endDate)
                .status("ACTIVE")
                .autoRenew(true)
                .build();

        subscription = subscriptionRepository.save(subscription);
        return convertToResponse(subscription);
    }

    public SubscriptionDTO.SubscriptionResponse getCurrentSubscription(Long userId) {
        Subscription subscription = subscriptionRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        return convertToResponse(subscription);
    }

    public void cancelSubscription(Long userId) {
        Subscription subscription = subscriptionRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        subscription.setStatus("CANCELLED");
        subscription.setAutoRenew(false);
        subscriptionRepository.save(subscription);
    }

    public SubscriptionDTO.SubscriptionResponse renewSubscription(Long userId) {
        // Cancel current subscription
        Subscription currentSub = subscriptionRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        currentSub.setStatus("EXPIRED");
        subscriptionRepository.save(currentSub);

        // Create new subscription
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(1);

        Subscription newSubscription = Subscription.builder()
                .user(user)
                .plan(currentSub.getPlan())
                .startDate(startDate)
                .endDate(endDate)
                .status("ACTIVE")
                .autoRenew(true)
                .build();

        newSubscription = subscriptionRepository.save(newSubscription);
        return convertToResponse(newSubscription);
    }

    public List<SubscriptionDTO.SubscriptionResponse> getSubscriptionHistory(Long userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return subscriptions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private SubscriptionDTO.SubscriptionResponse convertToResponse(Subscription subscription) {
        return SubscriptionDTO.SubscriptionResponse.builder()
                .id(subscription.getId())
                .userId(subscription.getUser().getId())
                .plan(subscription.getPlan())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .autoRenew(subscription.getAutoRenew())
                .createdAt(subscription.getCreatedAt())
                .build();
    }
}
