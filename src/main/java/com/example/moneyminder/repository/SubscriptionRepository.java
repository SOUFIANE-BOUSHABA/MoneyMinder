package com.example.moneyminder.repository;

import com.example.moneyminder.entity.Subscription;
import com.example.moneyminder.entity.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserId(Long userId);
    List<Subscription> findByStatus(SubscriptionStatus status);
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.endDate < CURRENT_DATE")
    List<Subscription> findExpiredSubscriptions();
}
