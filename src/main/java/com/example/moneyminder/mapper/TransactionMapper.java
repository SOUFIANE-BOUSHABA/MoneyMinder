package com.example.moneyminder.mapper;

import com.example.moneyminder.DTOs.TransactionRequest;
import com.example.moneyminder.VMs.TransactionVM;
import com.example.moneyminder.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "categoryName", source = "category.name")
    TransactionVM toVM(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    Transaction toEntity(TransactionRequest request);
}