package com.example.moneyminder.service;

import com.example.moneyminder.DTOs.FinancialReportRequest;
import com.example.moneyminder.VMs.FinancialReportVM;

import java.util.List;

public interface FinancialReportService {
    FinancialReportVM generateFinancialReport(FinancialReportRequest request);

    List<FinancialReportVM> getAllReportsForUser();

    FinancialReportVM getReportById(Long id);

    byte[] downloadReport(Long id);

    void scheduleMonthlyReportGeneration();

    void scheduleAnnualReportGeneration();
}
