package com.example.moneyminder.service;

import com.example.moneyminder.DTOs.TransactionRequest;
import com.example.moneyminder.VMs.TransactionVM;
import com.example.moneyminder.entity.User;

import java.util.List;

public interface TransactionService {
    TransactionVM createTransaction(TransactionRequest request);

    TransactionVM updateTransaction(Long id, TransactionRequest request);

    TransactionVM getTransactionById(Long id);

    List<TransactionVM> getAllTransactions();

    List<TransactionVM> getTransactionsByUserId(Long userId);

    void deleteTransaction(Long id);

    List<TransactionVM> getTransactionsForCurrentUserOrAdmin();

    void checkTransactionOwnership(Long transactionId);

    void checkTransactionAccess(Long transactionId);

    void checkUserAccess(Long userId);

    Double calculateCashFlow(Long userId);

    User getCurrentUser();
}
