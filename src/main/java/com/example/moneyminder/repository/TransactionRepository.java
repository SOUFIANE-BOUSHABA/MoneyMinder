package com.example.moneyminder.repository;

import com.example.moneyminder.entity.Transaction;
import com.example.moneyminder.entity.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByAccountId(Long accountId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = :type AND t.user.id = :userId")
    Optional<Double> sumByTypeAndUserId(TransactionType type, Long userId);

    List<Transaction> findAllByUser_IdAndDateBetween(Long userId, Date startDate, Date endDate);

    @Query("SELECT t.type, SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId GROUP BY t.type")
    List<Object[]> sumByTypeForUser(@Param("userId") Long userId);

    @Query("SELECT " +
            "EXTRACT(MONTH FROM t.date) AS month, " +
            "EXTRACT(YEAR FROM t.date) AS year, " +
            "t.type AS type, " +
            "SUM(t.amount) AS total " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "GROUP BY EXTRACT(YEAR FROM t.date), EXTRACT(MONTH FROM t.date), t.type")
    List<Object[]> monthlyTransactionSummaryForUser(@Param("userId") Long userId);



}
