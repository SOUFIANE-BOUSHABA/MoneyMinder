package com.example.moneyminder.service;

import com.example.moneyminder.DTOs.SubscriptionRequest;
import com.example.moneyminder.VMs.SubscriptionVM;

import java.util.List;

public interface SubscriptionService {
    SubscriptionVM requestSubscription(Long userId, SubscriptionRequest requestDTO);

    List<SubscriptionVM> getSubscriptionsForUser(Long userId);

    List<SubscriptionVM> getAllPendingSubscriptions();

    SubscriptionVM approveSubscription(Long subscriptionId);

    SubscriptionVM rejectSubscription(Long subscriptionId);

    void handleExpiredSubscriptions();
}
