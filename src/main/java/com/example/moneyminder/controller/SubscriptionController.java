package com.example.moneyminder.controller;

import com.example.moneyminder.DTOs.SubscriptionRequest;
import com.example.moneyminder.VMs.SubscriptionVM;
import com.example.moneyminder.exception.ResourceNotFoundException;
import com.example.moneyminder.repository.UserRepository;
import com.example.moneyminder.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.moneyminder.entity.User;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    @PostMapping("/request")
    public ResponseEntity<SubscriptionVM> requestSubscription(@RequestBody SubscriptionRequest requestDTO) {
        Long userId = getCurrentUserId();
        SubscriptionVM response = subscriptionService.requestSubscription(userId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionVM>> getUserSubscriptions() {
        Long userId = getCurrentUserId();
        List<SubscriptionVM> subscriptions = subscriptionService.getSubscriptionsForUser(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<SubscriptionVM>> getPendingSubscriptions() {
        List<SubscriptionVM> subscriptions = subscriptionService.getAllPendingSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<SubscriptionVM> approveSubscription(@PathVariable Long id) {
        SubscriptionVM response = subscriptionService.approveSubscription(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/reject/{id}")
    public ResponseEntity<SubscriptionVM> rejectSubscription(@PathVariable Long id) {
        SubscriptionVM response = subscriptionService.rejectSubscription(id);
        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return currentUser.getId();
    }
}