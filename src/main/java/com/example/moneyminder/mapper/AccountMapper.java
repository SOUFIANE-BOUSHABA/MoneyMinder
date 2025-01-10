package com.example.moneyminder.mapper;

import com.example.moneyminder.DTOs.AccountRequest;
import com.example.moneyminder.VMs.AccountVM;
import com.example.moneyminder.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "userId", source = "user.id")
    AccountVM toVM(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    Account toEntity(AccountRequest request);
}
