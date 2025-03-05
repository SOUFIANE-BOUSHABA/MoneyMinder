package com.example.moneyminder.mapper;

import com.example.moneyminder.DTOs.SubscriptionRequest;
import com.example.moneyminder.VMs.SubscriptionVM;
import com.example.moneyminder.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "planName", source = "subscription.subscriptionPlan.name")
    @Mapping(target = "price", source = "subscription.subscriptionPlan.price")
    SubscriptionVM toVM(Subscription subscription);

    Subscription toEntity(SubscriptionRequest subscriptionRequest);
}
