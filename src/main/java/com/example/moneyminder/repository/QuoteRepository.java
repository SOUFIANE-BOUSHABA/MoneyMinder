package com.example.moneyminder.repository;

import com.example.moneyminder.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    boolean existsByQuoteNumber(String quoteNumber);
}
