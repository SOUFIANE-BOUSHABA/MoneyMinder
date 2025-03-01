package com.example.moneyminder.repository;

import com.example.moneyminder.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    boolean existsByQuoteNumber(String quoteNumber);
    List<Quote> findAllByUser_IdAndIssueDateBetween(Long userId, Date startDate, Date endDate);
    List<Quote> findAllByUser_Id(Long userId);

    @Query("SELECT q.status, COUNT(q) FROM Quote q WHERE q.user.id = :userId GROUP BY q.status")
    List<Object[]> countQuotesByStatusForUser(@Param("userId") Long userId);


}
