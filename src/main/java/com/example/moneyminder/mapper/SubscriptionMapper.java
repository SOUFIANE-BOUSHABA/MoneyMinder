package com.example.moneyminder.mapper;

import com.example.moneyminder.DTOs.SubscriptionRequest;
import com.example.moneyminder.VMs.SubscriptionVM;
import com.example.moneyminder.entity.Subscription;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionVM toVM(Subscription subscription);

    Subscription toEntity(SubscriptionRequest subscriptionRequest);
}
