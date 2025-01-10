package com.example.moneyminder.service.impl;

import com.example.moneyminder.DTOs.TransactionRequest;
import com.example.moneyminder.VMs.TransactionVM;
import com.example.moneyminder.entity.Account;
import com.example.moneyminder.entity.Category;
import com.example.moneyminder.entity.Transaction;
import com.example.moneyminder.entity.User;
import com.example.moneyminder.entity.enums.TransactionType;
import com.example.moneyminder.exception.AccessDeniedException;
import com.example.moneyminder.exception.ResourceNotFoundException;
import com.example.moneyminder.mapper.TransactionMapper;
import com.example.moneyminder.repository.AccountRepository;
import com.example.moneyminder.repository.CategoryRepository;
import com.example.moneyminder.repository.TransactionRepository;
import com.example.moneyminder.repository.UserRepository;
import com.example.moneyminder.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    public TransactionVM createTransaction(TransactionRequest request) {
        User currentUser = getCurrentUser();

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + request.getAccountId()));

        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setCategory(category);
        transaction.setUser(currentUser);
        transaction.setAccount(account);

        // Adjust account balance
        if (TransactionType.valueOf(request.getType()) == TransactionType.INCOME) {
            account.setBalance(account.getBalance() + request.getAmount());
        } else {
            account.setBalance(account.getBalance() - request.getAmount());
        }
        accountRepository.save(account);

        return transactionMapper.toVM(transactionRepository.save(transaction));
    }

    @Override
    public TransactionVM updateTransaction(Long id, TransactionRequest request) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));

        Account account = existingTransaction.getAccount();

        // Revert previous balance adjustments
        if (existingTransaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance() - existingTransaction.getAmount());
        } else {
            account.setBalance(account.getBalance() + existingTransaction.getAmount());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        existingTransaction.setDate(request.getDate());
        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setType(TransactionType.valueOf(request.getType()));
        existingTransaction.setCategory(category);
        existingTransaction.setDescription(request.getDescription());

        // Adjust account balance with new transaction data
        if (TransactionType.valueOf(request.getType()) == TransactionType.INCOME) {
            account.setBalance(account.getBalance() + request.getAmount());
        } else {
            account.setBalance(account.getBalance() - request.getAmount());
        }
        accountRepository.save(account);

        return transactionMapper.toVM(transactionRepository.save(existingTransaction));
    }

    @Override
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));

        checkTransactionOwnership(id);

        Account account = transaction.getAccount();

        // Revert balance adjustments
        if (transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance() - transaction.getAmount());
        } else {
            account.setBalance(account.getBalance() + transaction.getAmount());
        }
        accountRepository.save(account);

        transactionRepository.delete(transaction);
    }

    @Override
    public TransactionVM getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        return transactionMapper.toVM(transaction);
    }

    @Override
    public List<TransactionVM> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toVM)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionVM> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId).stream()
                .map(transactionMapper::toVM)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionVM> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(transactionMapper::toVM)
                .collect(Collectors.toList());
    }

    @Override
    public void checkTransactionOwnership(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));

        User currentUser = getCurrentUser();
        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to modify or delete this transaction.");
        }
    }

    @Override
    public User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
