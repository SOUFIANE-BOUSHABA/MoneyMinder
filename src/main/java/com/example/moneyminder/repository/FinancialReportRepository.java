package com.example.moneyminder.repository;

import com.example.moneyminder.entity.FinancialReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FinancialReportRepository extends JpaRepository<FinancialReport, Long> {
    List<FinancialReport> findAllByUserId(Long userId);
    List<FinancialReport> findAllByUserIdAndGenerationDateBetween(Long userId, Date start, Date end);
}
