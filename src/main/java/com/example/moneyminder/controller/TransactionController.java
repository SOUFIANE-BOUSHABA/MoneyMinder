package com.example.moneyminder.controller;

import com.example.moneyminder.DTOs.TransactionRequest;
import com.example.moneyminder.VMs.TransactionVM;
import com.example.moneyminder.entity.User;
import com.example.moneyminder.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionVM> createTransaction(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.createTransaction(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionVM> updateTransaction(@PathVariable Long id, @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionVM> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping
    public ResponseEntity<List<TransactionVM>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getTransactionsForCurrentUserOrAdmin());
    }

    @GetMapping("/user/cashflow")
    public ResponseEntity<Double> getCashFlowForCurrentUser() {
        User currentUser = transactionService.getCurrentUser();
        double cashFlow = transactionService.calculateCashFlow(currentUser.getId());
        return ResponseEntity.ok(cashFlow);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionVM>> getTransactionsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getTransactionsByUserId(userId));
    }
}
