package com.example.moneyminder.service;

import com.example.moneyminder.DTOs.AccountRequest;
import com.example.moneyminder.VMs.AccountVM;

import java.util.List;

public interface AccountService {
    AccountVM createAccount(AccountRequest request);
    AccountVM updateAccount(Long id, AccountRequest request);
    AccountVM getAccountById(Long id);
    List<AccountVM> getAllAccounts();
    List<AccountVM> getAllAccountsByUserId(Long userId);
    void deleteAccount(Long id);
}
