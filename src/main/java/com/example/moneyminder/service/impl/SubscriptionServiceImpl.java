package com.example.moneyminder.service.impl;

import com.example.moneyminder.DTOs.SubscriptionRequest;
import com.example.moneyminder.VMs.SubscriptionVM;
import com.example.moneyminder.entity.Subscription;
import com.example.moneyminder.entity.SubscriptionPlan;
import com.example.moneyminder.entity.User;
import com.example.moneyminder.entity.enums.SubscriptionStatus;
import com.example.moneyminder.exception.ResourceNotFoundException;
import com.example.moneyminder.mapper.SubscriptionMapper;
import com.example.moneyminder.repository.SubscriptionPlanRepository;
import com.example.moneyminder.repository.SubscriptionRepository;
import com.example.moneyminder.repository.UserRepository;
import com.example.moneyminder.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserRepository userRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    public SubscriptionVM requestSubscription(Long userId, SubscriptionRequest requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(requestDTO.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + requestDTO.getPlanId()));

        Subscription subscription = Subscription.builder()
                .user(user)
                .subscriptionPlan(plan)
                .status(SubscriptionStatus.PENDING)
                .requestDate(new Date())
                .build();

        subscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toVM(subscription);
    }

    @Override
    public List<SubscriptionVM> getSubscriptionsForUser(Long userId) {
        return subscriptionRepository.findByUserId(userId).stream()
                .map(subscriptionMapper::toVM)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionVM> getAllPendingSubscriptions() {
        return subscriptionRepository.findByStatus(SubscriptionStatus.PENDING).stream()
                .map(subscriptionMapper::toVM)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionVM approveSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(new Date());
        subscription.setEndDate(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)); // 30 days

        subscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toVM(subscription);
    }

    @Override
    public SubscriptionVM rejectSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        subscription.setStatus(SubscriptionStatus.REJECTED);
        subscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toVM(subscription);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?") // Daily check at midnight
    public void handleExpiredSubscriptions() {
        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions();
        for (Subscription subscription : expiredSubscriptions) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(subscription);
        }
    }
}