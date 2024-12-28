package com.example.moneyminder.service.impl;

import com.example.moneyminder.DTOs.TransactionRequest;
import com.example.moneyminder.VMs.TransactionVM;
import com.example.moneyminder.entity.Transaction;
import com.example.moneyminder.entity.User;
import com.example.moneyminder.entity.enums.TransactionType;
import com.example.moneyminder.exception.AccessDeniedException;
import com.example.moneyminder.exception.ResourceNotFoundException;
import com.example.moneyminder.mapper.TransactionMapper;
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


    @Override
    public TransactionVM createTransaction(TransactionRequest request) {
        User currentUser = getCurrentUser();

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setCategory(category);
        transaction.setUser(currentUser);


        double previousBalance = transactionRepository.findLastBalanceByUserId(currentUser.getId()).orElse(0.0);
        transaction.setBalance(
                request.getType().equals("INCOME")
                        ? previousBalance + request.getAmount()
                        : previousBalance - request.getAmount()
        );

        return transactionMapper.toVM(transactionRepository.save(transaction));
    }

    @Override
    public TransactionVM updateTransaction(Long id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));

        checkTransactionOwnership(id);

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));


        double previousBalance = transactionRepository.findLastBalanceByUserId(transaction.getUser().getId()).orElse(0.0);


        double adjustedBalance = transaction.getType() == TransactionType.INCOME
                ? previousBalance - transaction.getAmount()
                : previousBalance + transaction.getAmount();

        transaction.setDate(request.getDate());
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.valueOf(request.getType()));
        transaction.setCategory(category);
        transaction.setDescription(request.getDescription());

        transaction.setBalance(
                request.getType().equals("INCOME")
                        ? adjustedBalance + request.getAmount()
                        : adjustedBalance - request.getAmount()
        );

        return transactionMapper.toVM(transactionRepository.save(transaction));
    }


    @Override
    public TransactionVM getTransactionById(Long id) {
        checkTransactionAccess(id);
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
    public List<TransactionVM> getTransactionsByUserId(Long userId) {
        checkUserAccess(userId);
        return transactionRepository.findByUserId(userId).stream()
                .map(transactionMapper::toVM)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTransaction(Long id) {
        checkTransactionOwnership(id);
        transactionRepository.deleteById(id);
    }

    @Override
    public List<TransactionVM> getTransactionsForCurrentUserOrAdmin() {
        User currentUser = getCurrentUser();
        if (currentUser.getRole().getName().equals("ROLE_ADMIN")) {
            return getAllTransactions();
        } else {
            return getTransactionsByUserId(currentUser.getId());
        }
    }

    @Override
    public Double calculateCashFlow(Long userId) {
        checkUserAccess(userId);
        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        return transactions.stream()
                .mapToDouble(transaction ->
                        transaction.getType() == TransactionType.INCOME
                                ? transaction.getAmount()
                                : -transaction.getAmount()
                )
                .sum();
    }

    @Override
    public void checkTransactionOwnership(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));

        User currentUser = getCurrentUser();
        if (!transaction.getUser().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().getName().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("You are not authorized to modify this transaction.");
        }
    }

    @Override
    public void checkTransactionAccess(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));

        User currentUser = getCurrentUser();
        if (!transaction.getUser().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().getName().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("You are not authorized to view this transaction.");
        }
    }

    @Override
    public void checkUserAccess(Long userId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId) &&
                !currentUser.getRole().getName().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("You are not authorized to view this user's transactions.");
        }
    }

    @Override
    public User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
