package com.example.moneyminder.service.impl;

import com.example.moneyminder.DTOs.AccountRequest;
import com.example.moneyminder.VMs.AccountVM;
import com.example.moneyminder.entity.Account;
import com.example.moneyminder.entity.Subscription;
import com.example.moneyminder.entity.User;
import com.example.moneyminder.entity.enums.SubscriptionStatus;
import com.example.moneyminder.exception.CustomException;
import com.example.moneyminder.exception.ResourceNotFoundException;
import com.example.moneyminder.mapper.AccountMapper;
import com.example.moneyminder.repository.AccountRepository;
import com.example.moneyminder.repository.SubscriptionRepository;
import com.example.moneyminder.repository.UserRepository;
import com.example.moneyminder.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;
    private final SubscriptionRepository subscriptionRepository;


    public User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    @Override
    public AccountVM createAccount(AccountRequest request) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId).stream()
                .filter(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE)
                .collect(Collectors.toList());
        boolean isPremium = false;
        if (!subscriptions.isEmpty()) {
            isPremium = subscriptions.get(0).getSubscriptionPlan().getPrice() > 0;
        }

        List<Account> accounts = accountRepository.findAllByUserId(userId);
        if (!isPremium && accounts.size() >= 2) {
            throw new CustomException("Free plan allows a maximum of 2 accounts. Please upgrade to Premium for more accounts.");
        }

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
    public List<AccountVM> getAllAccountsByUserId() {
        Long userId = getCurrentUserId();
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
