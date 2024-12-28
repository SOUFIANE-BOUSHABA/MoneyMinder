package com.example.moneyminder.repository;

import com.example.moneyminder.entity.Transaction;
import com.example.moneyminder.entity.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserId(Long userId);

    @Query("SELECT t.balance FROM Transaction t WHERE t.user.id = :userId ORDER BY t.date DESC, t.id DESC LIMIT 1")
    Optional<Double> findLastBalanceByUserId(@Param("userId") Long userId);


    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = :type AND t.user.id = :userId")
    Optional<Double> sumByTypeAndUserId(TransactionType type, Long userId);
}
