package com.example.moneyminder.service.impl;

import com.example.moneyminder.DTOs.AccountRequest;
import com.example.moneyminder.VMs.AccountVM;
import com.example.moneyminder.entity.Account;
import com.example.moneyminder.entity.User;
import com.example.moneyminder.exception.ResourceNotFoundException;
import com.example.moneyminder.mapper.AccountMapper;
import com.example.moneyminder.repository.AccountRepository;
import com.example.moneyminder.repository.UserRepository;
import com.example.moneyminder.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountVM createAccount(AccountRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        Account account = accountMapper.toEntity(request);
        account.setUser(user);

        return accountMapper.toVM(accountRepository.save(account));
    }

    @Override
    public AccountVM updateAccount(Long id, AccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));

        account.setAccountName(request.getAccountName());
        account.setBalance(request.getBalance());

        return accountMapper.toVM(accountRepository.save(account));
    }

    @Override
    public AccountVM getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
        return accountMapper.toVM(account);
    }

    @Override
    public List<AccountVM> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toVM)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountVM> getAllAccountsByUserId(Long userId) {
        return accountRepository.findAllByUserId(userId).stream()
                .map(accountMapper::toVM)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
        accountRepository.delete(account);
    }
}
