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

    List<TransactionVM> getTransactionsByAccountId(Long accountId);
    List<TransactionVM> getTransactionsByUserId(Long userId);

    void deleteTransaction(Long id);

    void checkTransactionOwnership(Long transactionId);

    User getCurrentUser();
}
