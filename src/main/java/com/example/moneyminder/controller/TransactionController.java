package com.example.moneyminder.controller;

import com.example.moneyminder.DTOs.TransactionRequest;
import com.example.moneyminder.VMs.TransactionVM;
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

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionVM>> getTransactionsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId));
    }

    @GetMapping
    public ResponseEntity<List<TransactionVM>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }


    @GetMapping("/total-income")
    public ResponseEntity<Double> getTotalIncome() {
        return ResponseEntity.ok(transactionService.getTotalIncome());
    }

    @GetMapping("/total-expenses")
    public ResponseEntity<Double> getTotalExpenses() {
        return ResponseEntity.ok(transactionService.getTotalExpenses());
    }

    @GetMapping("/income-change-percentage")
    public ResponseEntity<Double> getIncomeChangePercentage() {
        return ResponseEntity.ok(transactionService.getIncomeChangePercentage());
    }

    @GetMapping("/expense-change-percentage")
    public ResponseEntity<Double> getExpenseChangePercentage() {
        return ResponseEntity.ok(transactionService.getExpenseChangePercentage());
    }
}
