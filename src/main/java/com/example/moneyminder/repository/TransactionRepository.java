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
    Optional<Double> sumByTypeAndUserId(@Param("type") TransactionType type, @Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type")
    Double sumByUserIdAndType(@Param("userId") Long userId, @Param("type") TransactionType type);

    @Query(value = """
   SELECT COALESCE(SUM(t.amount), 0) 
   FROM transactions t
   WHERE t.user_id = :userId
     AND t.type = :type
     AND date_trunc('month', t.date) = date_trunc('month', CURRENT_DATE - interval '1 month')
""", nativeQuery = true)
    Double sumByUserIdAndTypeLastMonth(@Param("userId") Long userId, @Param("type") String type);


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

    List<Transaction> findAllByUserIdAndDateBetween(@Param("userId") Long userId,
                                                    @Param("startDate") Date startDate,
                                                    @Param("endDate") Date endDate);
}
