package com.example.moneyminder.controller;

import com.example.moneyminder.DTOs.FinancialReportRequest;
import com.example.moneyminder.VMs.FinancialReportVM;
import com.example.moneyminder.service.FinancialReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class FinancialReportController {

    private final FinancialReportService financialReportService;


    @PostMapping
    public ResponseEntity<FinancialReportVM> generateFinancialReport(@RequestBody FinancialReportRequest request) {
        FinancialReportVM report = financialReportService.generateFinancialReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FinancialReportVM>> getAllReportsForUser(@PathVariable Long userId) {
        List<FinancialReportVM> reports = financialReportService.getAllReportsForUser(userId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialReportVM> getReportById(@PathVariable Long id) {
        FinancialReportVM report = financialReportService.getReportById(id);
        return ResponseEntity.ok(report);
    }


    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long id) {
        byte[] pdfContent = financialReportService.downloadReport(id);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/pdf");
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=financial_report_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }
}
