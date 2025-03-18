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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User user;
    private Account account;
    private AccountRequest accountRequest;
    private AccountVM accountVM;

    @BeforeEach
    public void setUp() {
        org.springframework.security.core.userdetails.User dummyPrincipal = new org.springframework.security.core.userdetails.User("test@example.com", "password", List.of());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(dummyPrincipal, null, dummyPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        account = new Account();
        account.setId(1L);
        account.setAccountName("Test Account");
        account.setBalance(100.0);
        account.setUser(user);

        accountRequest = new AccountRequest();
        accountRequest.setAccountName("Test Account");
        accountRequest.setBalance(100.0);

        accountVM = new AccountVM();
        accountVM.setId(1L);
        accountVM.setAccountName("Test Account");
        accountVM.setBalance(100.0);
    }

    @AfterEach
    public void tearDown() {

        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCreateAccount_FreePlanAllowed() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(subscriptionRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        when(accountRepository.findAllByUserId(1L)).thenReturn(Collections.emptyList());

        when(accountMapper.toEntity(accountRequest)).thenReturn(account);

        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(accountMapper.toVM(any(Account.class))).thenReturn(accountVM);

        AccountVM result = accountService.createAccount(accountRequest);

        assertThat(result).isNotNull();
        assertThat(result.getAccountName()).isEqualTo("Test Account");
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testCreateAccount_FreePlanLimitExceeded() {

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(subscriptionRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        List<Account> existingAccounts = List.of(new Account(), new Account());
        when(accountRepository.findAllByUserId(1L)).thenReturn(existingAccounts);

        assertThatThrownBy(() -> accountService.createAccount(accountRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("Free plan allows a maximum of 2 accounts");

        verify(accountRepository, never()).save(any());
    }

    @Test
    public void testCreateAccount_PremiumPlanAllowsMore() {

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Subscription subscription = new Subscription();
        com.example.moneyminder.entity.SubscriptionPlan plan = new com.example.moneyminder.entity.SubscriptionPlan();
        plan.setPrice(49.0);
        subscription.setSubscriptionPlan(plan);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        when(subscriptionRepository.findByUserId(1L)).thenReturn(List.of(subscription));

        List<Account> existingAccounts = List.of(new Account(), new Account(), new Account());
        when(accountRepository.findAllByUserId(1L)).thenReturn(existingAccounts);

        when(accountMapper.toEntity(accountRequest)).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(accountMapper.toVM(any(Account.class))).thenReturn(accountVM);

        AccountVM result = accountService.createAccount(accountRequest);
        assertThat(result).isNotNull();
        verify(accountRepository, times(1)).save(account);
    }



    @Test
    public void testUpdateAccount_NotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());
        AccountRequest updateRequest = new AccountRequest();
        updateRequest.setAccountName("Updated");
        updateRequest.setBalance(150.0);

        assertThatThrownBy(() -> accountService.updateAccount(99L, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found with ID: 99");
    }

    @Test
    public void testGetAccountById_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountMapper.toVM(account)).thenReturn(accountVM);

        AccountVM result = accountService.getAccountById(1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    public void testGetAccountById_NotFound() {
        when(accountRepository.findById(50L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> accountService.getAccountById(50L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found with ID: 50");
    }

    @Test
     void testGetAllAccounts() {
        List<Account> accounts = List.of(account);
        when(accountRepository.findAll()).thenReturn(accounts);
        when(accountMapper.toVM(account)).thenReturn(accountVM);

        List<AccountVM> result = accountService.getAllAccounts();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAccountName()).isEqualTo("Test Account");
    }

    @Test
    public void testGetAllAccountsByUserId() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        List<Account> accounts = List.of(account);
        when(accountRepository.findAllByUserId(1L)).thenReturn(accounts);
        when(accountMapper.toVM(account)).thenReturn(accountVM);

        List<AccountVM> result = accountService.getAllAccountsByUserId();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAccountName()).isEqualTo("Test Account");
    }

    @Test
    public void testDeleteAccount_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        accountService.deleteAccount(1L);
        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    public void testDeleteAccount_NotFound() {
        when(accountRepository.findById(10L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> accountService.deleteAccount(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found with ID: 10");
    }
}